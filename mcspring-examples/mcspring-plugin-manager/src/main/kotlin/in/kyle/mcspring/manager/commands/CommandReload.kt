package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.commands.dsl.command
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import `in`.kyle.mcspring.commands.dsl.mcspring.Command
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
class CommandReload {

    @Command(
            value = "reload",
            aliases = ["rl"],
            description = "Reload the server"
    )
    fun reload() = command {
        then {
            sender.sendMessage("Reloading the server...")
            Bukkit.getServer().reload()
        }
    }
}
