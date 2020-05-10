package `in`.kyle.mcspring.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller

@Controller
@ConditionalOnBean(Plugin::class)
internal class CommandController(
        private val plugin: Plugin
) {
    private val commandMap = getCommandMap()

    fun registerCommand(command: Command) {
        commandMap.register(command.label, plugin.name, command)
    }

    private fun getCommandMap(): CommandMap {
        val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        bukkitCommandMap.isAccessible = true
        return bukkitCommandMap[Bukkit.getServer()] as CommandMap
    }

    @Bean
    fun commandMap(): CommandMap {
        return commandMap
    }
}
