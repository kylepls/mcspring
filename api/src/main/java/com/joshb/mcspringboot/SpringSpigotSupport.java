package com.joshb.mcspringboot;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackageClasses = SpringPlugin.class)
@Configuration
class SpringSpigotSupport {
    
    @Bean(destroyMethod = "")
    Plugin plugin(@Value("${spigot.plugin}") String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }
    
    @Bean(destroyMethod = "")
    Server server(Plugin plugin) {
        return plugin.getServer();
    }
    
    @Bean(destroyMethod = "")
    PluginManager pluginManager(Server server) {
        return server.getPluginManager();
    }
    
    @Bean(destroyMethod = "")
    ScoreboardManager scoreboardManager(Server server) {
        return server.getScoreboardManager();
    }
    
    @Bean(destroyMethod = "")
    Messenger messenger(Server server) {
        return server.getMessenger();
    }
    
    @Bean(destroyMethod = "")
    FileConfiguration configuration(Plugin plugin) {
        return plugin.getConfig();
    }
    
    @Bean(destroyMethod = "")
    PluginDescriptionFile description(Plugin plugin) {
        return plugin.getDescription();
    }
    
    @Bean(destroyMethod = "")
    BukkitScheduler scheduler(Server server) {
        return server.getScheduler();
    }
}