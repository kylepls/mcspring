package in.kyle.mcspring.manager.controller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;

@Lazy
@Component
@RequiredArgsConstructor
@ConditionalOnBean(Plugin.class)
class BukkitPluginUnloader {
    
    private final PluginManager pluginManager;
    private final CommandMap commandMap;
    private Map<String, Command> commands;
    private List<Plugin> plugins;
    private Map<String, Plugin> names;
    
    @PostConstruct
    @SneakyThrows
    void setup() {
        plugins = getDeclaredField(pluginManager, "plugins");
        names = getDeclaredField(pluginManager, "lookupNames");
        Field knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommands.setAccessible(true);
        commands = (Map<String, Command>) knownCommands.get(commandMap);
    }
    
    @SneakyThrows
    boolean unload(Plugin plugin) {
        pluginManager.disablePlugin(plugin);
        
        synchronized (pluginManager) {
            plugins.remove(plugin);
            names.remove(plugin.getName());
            unregisterCommands(plugin);
            
            closeClassLoader(plugin.getClass().getClassLoader());
        }
        System.gc();
        
        return true;
    }
    
    private void unregisterCommands(Plugin plugin) {
        var unregister = commands.entrySet()
                .stream()
                .filter(e -> e.getValue() instanceof PluginCommand)
                .filter(e -> ((PluginCommand) e.getValue()).getPlugin() == plugin)
                .peek(e -> e.getValue().unregister(commandMap))
                .collect(Collectors.toSet());
        unregister.forEach(e -> commands.remove(e.getKey()));
    }
    
    @SneakyThrows
    private void closeClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            setDeclaredField(classLoader, "plugin", null);
            setDeclaredField(classLoader, "pluginInit", null);
            ((URLClassLoader) classLoader).close();
        }
    }
    
    @SneakyThrows
    private <T> T getDeclaredField(Object object, String fieldName) {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(object);
    }
    
    @SneakyThrows
    private void setDeclaredField(Object object, String fieldName, Object value) {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
