package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
internal class CommandOp {

    @Command(
            value = "op",
            description = "Toggle yourself or another players OP status",
            usage = "/op <player>?"
    )
    fun op() = commandExecutor {
        val target by playerArg {
            default { sender as? Player }
            invalid { message("Player $it not found") }
        }
        then { message(toggleOp(target)) }
    }

    private fun toggleOp(target: CommandSender): String {
        target.isOp = !target.isOp
        val modifier = if (target.isOp) "now" else "no longer"
        return "${target.name} is $modifier op"
    }
}
