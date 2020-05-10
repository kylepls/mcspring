package `in`.kyle.mcspring

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.messaging.Messenger
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scoreboard.ScoreboardManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.logging.Logger

@Configuration
@ComponentScan(basePackageClasses = [SpringPlugin::class])
@EnableScheduling
@EnableAspectJAutoProxy
internal open class SpringSpigotSupport {
    @Bean
    open fun plugin(@Value("\${spigot.plugin}") pluginName: String): Plugin {
        return Bukkit.getPluginManager().getPlugin(pluginName)!!
    }

    @Bean(destroyMethod = "")
    open fun server(plugin: Plugin): Server {
        return plugin.server
    }

    @Bean
    open fun logger(plugin: Plugin): Logger {
        return plugin.logger
    }

    @Bean
    open fun pluginManager(server: Server): PluginManager {
        return server.pluginManager
    }

    @Bean
    open fun scoreboardManager(server: Server): ScoreboardManager? {
        return server.scoreboardManager
    }

    @Bean
    open fun messenger(server: Server): Messenger {
        return server.messenger
    }

    @Bean
    open fun configuration(plugin: Plugin): FileConfiguration {
        return plugin.config
    }

    @Bean
    open fun description(plugin: Plugin): PluginDescriptionFile {
        return plugin.description
    }

    @Bean
    open fun scheduler(server: Server): BukkitScheduler {
        return server.scheduler
    }

    @Bean
    open fun pluginLoader(plugin: Plugin): PluginLoader {
        return plugin.pluginLoader
    }
}
