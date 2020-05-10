package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandImpl
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
internal class CommandSpeed {

    @Command(value = "speed", description = "Set your movement and fly speed", usage = "/speed <player> <speed>")
    fun speed(command: PluginCommandImpl) {
        command.requiresPlayerSender { "Sender must be a player" }
        command.withDouble("Speed value must be an integer")
        command.then(::speedExecutor)
        command.otherwise("Usage: /speed <value>")
    }

    private fun speedExecutor(sender: Player, speed: Double): String {
        sender.flySpeed = speed.toFloat()
        sender.walkSpeed = speed.toFloat()
        return "Speed set to $speed"
    }
}
