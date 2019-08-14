package in.kyle.mcspring.manager.commands;

import org.bukkit.plugin.Plugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Collectors;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.manager.controller.PluginController;
import in.kyle.mcspring.subcommands.PluginCommand;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(Plugin.class)
class CommandPlugin {
    
    private final PluginController pluginController;
    
    @Command(value = "plugin",
             aliases = "pl",
             description = "Load/unload/reload a specific plugin",
             usage = "/plugin <load|unload|list>")
    void plugin(PluginCommand command) {
        command.on("load", this::load);
        command.on("unload", this::unload);
        command.on("list", this::list);
        command.otherwise("Usage: /plugin <load|unload|list>");
    }
    
    private void load(PluginCommand command) {
        command.withMap(pluginController.getLoadablePlugins(),
                        s -> String.format("Plugin %s not found or is already loaded", s));
        command.then(this::executeLoad);
        command.otherwise("Usage: /plugin load <name>");
    }
    
    private void unload(PluginCommand command) {
        var plugins = pluginController.getPlugins()
                .stream()
                .collect(Collectors.toMap(org.bukkit.plugin.Plugin::getName, Function.identity()));
        command.withMap(plugins, s -> String.format("Plugin %s is not loaded", s));
        command.then(this::executeDisable);
        command.otherwise("Usage: /plugin unload <name>");
    }
    
    private void list(PluginCommand command) {
        command.then(this::executeListPlugins);
    }
    
    private String executeListPlugins() {
        return pluginController.getAllPlugins()
                .entrySet()
                .stream()
                .map(e -> String.format("%s%s", e.getValue() ? "&1" : "&4", e.getKey()))
                .collect(Collectors.joining(" "));
    }
    
    private String executeLoad(Path jar) {
        var pluginOptional = pluginController.load(jar);
        if (pluginOptional.isPresent()) {
            return String.format("Plugin %s enabled", pluginOptional.get().getName());
        } else {
            return String.format("&4Could not load %s see log for details", jar);
        }
    }
    
    private String executeDisable(org.bukkit.plugin.Plugin plugin) {
        boolean disabled = pluginController.unload(plugin);
        if (disabled) {
            return String.format("Plugin %s disabled", plugin);
        } else {
            return String.format("Could not disable %s, see log for details", plugin);
        }
    }
}
