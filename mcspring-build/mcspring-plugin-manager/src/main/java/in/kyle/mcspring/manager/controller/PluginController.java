package in.kyle.mcspring.manager.controller;

import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PluginController {
    
    Optional<Plugin> load(Path jar);
    
    boolean unload(Plugin plugin);
    
    Map<String, Path> getLoadablePlugins();
    
    Optional<Plugin> getPlugin(String name);
    
    Set<Plugin> getPlugins();
    
    Map<String, Boolean> getAllPlugins();
}
