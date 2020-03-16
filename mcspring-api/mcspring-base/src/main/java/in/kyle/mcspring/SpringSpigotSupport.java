package in.kyle.mcspring;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.reflect.Field;
import java.util.logging.Logger;

@Configuration
@ComponentScan(basePackageClasses = SpringPlugin.class)
@EnableScheduling
@EnableAspectJAutoProxy
@RequiresSpigot
public class SpringSpigotSupport {
    
    @Bean
    Plugin plugin(@Value("${spigot.plugin}") String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }
    
    @Bean(destroyMethod = "")
    Server server(Plugin plugin) {
        return plugin.getServer();
    }
    
    @Bean
    Logger logger(Plugin plugin) {
        return plugin.getLogger();
    }
    
    @Bean
    PluginManager pluginManager(Server server) {
        return server.getPluginManager();
    }
    
    @Bean
    ScoreboardManager scoreboardManager(Server server) {
        return server.getScoreboardManager();
    }
    
    @Bean
    Messenger messenger(Server server) {
        return server.getMessenger();
    }
    
    @Bean
    FileConfiguration configuration(Plugin plugin) {
        return plugin.getConfig();
    }
    
    @Bean
    PluginDescriptionFile description(Plugin plugin) {
        return plugin.getDescription();
    }
    
    @Bean
    BukkitScheduler scheduler(Server server) {
        return server.getScheduler();
    }
    
    @Bean
    PluginLoader pluginLoader(Plugin plugin) {
        return plugin.getPluginLoader();
    }
    
    @Bean
    CommandMap getCommandMap(Server server) throws Exception {
        Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        return (CommandMap) bukkitCommandMap.get(server);
    }
}
