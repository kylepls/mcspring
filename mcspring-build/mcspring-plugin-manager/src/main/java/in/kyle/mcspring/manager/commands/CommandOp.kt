package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommand
import org.bukkit.command.CommandSender
import org.springframework.stereotype.Component

@Component
internal class CommandOp {
    @Command(value = "op", description = "Toggle yourself or another players OP status", usage = "/op <player>?")
    fun op(command: PluginCommand) {
        command.withPlayer { "Player $it not found" }
        command.then(::toggleOp)
        command.otherwise(::toggleOp)
    }

    private fun toggleOp(target: CommandSender): String {
        target.isOp = !target.isOp
        return "${target.name} is ${if (target.isOp) "now" else "no longer"} op"
    }
}
