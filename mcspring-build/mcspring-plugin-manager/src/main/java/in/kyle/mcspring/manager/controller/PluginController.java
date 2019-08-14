package in.kyle.mcspring.manager.controller;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;

@Controller
@RequiredArgsConstructor
@ConditionalOnBean(Plugin.class)
public class PluginController {
    
    private final PluginManager pluginManager;
    private final PluginLoader pluginLoader;
    private final BukkitPluginUnloader unloader;
    private final Logger logger;
    
    public Optional<Plugin> load(Path jar) {
        try {
            Plugin plugin = pluginManager.loadPlugin(jar.toFile());
            Optional<Plugin> optionalPlugin = Optional.ofNullable(plugin);
            optionalPlugin.ifPresent(Plugin::onLoad);
            return optionalPlugin;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not load " + jar.toAbsolutePath());
            logger.log(Level.SEVERE, e, () -> "");
            return Optional.empty();
        }
    }
    
    public boolean unload(Plugin plugin) {
        if (pluginManager.isPluginEnabled(plugin)) {
            try {
                return unloader.unload(plugin);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Could not unload " + plugin.getName());
                logger.log(Level.SEVERE, e, () -> "");
            }
        }
        return false;
    }
    
    @SneakyThrows
    public Map<String, Path> getLoadablePlugins() {
        Path pluginsFolder = Paths.get("plugins");
        return Files.list(pluginsFolder)
                .filter(p -> p.toString().endsWith(".jar"))
                .filter(jar -> getPluginName(jar).isPresent())
                .collect(Collectors.toMap(j -> getPluginName(j).get(), Function.identity()));
    }
    
    public Optional<Plugin> getPlugin(String name) {
        return Optional.ofNullable(pluginManager.getPlugin(name));
    }
    
    public Set<Plugin> getPlugins() {
        return new HashSet<>(Arrays.asList(pluginManager.getPlugins()));
    }
    
    public Map<String, Boolean> getAllPlugins() {
        Map<String, Boolean> allPlugins = new HashMap<>();
        getLoadablePlugins().forEach((k,v)->allPlugins.put(k, false));
        getPlugins().forEach(p -> allPlugins.put(p.getName(), p.isEnabled()));
        return allPlugins;
    }
    
    private boolean isEnabled(String pluginName) {
        return getPlugin(pluginName).map(Plugin::isEnabled).orElse(false);
    }
    
    private Optional<String> getPluginName(Path jar) {
        try {
            var description = pluginLoader.getPluginDescription(jar.toFile());
            return Optional.of(description.getName());
        } catch (InvalidDescriptionException e) {
            return Optional.empty();
        }
    }
    
}
