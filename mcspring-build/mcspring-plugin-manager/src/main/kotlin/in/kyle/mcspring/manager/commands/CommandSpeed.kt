package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.commands.dsl.commandExecutor
import `in`.kyle.mcspring.commands.dsl.mcspring.Command
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
internal class CommandSpeed {

    @Command(
            value = "speed",
            description = "Set your movement and fly speed",
            usage = "/speed <player> <speed>"
    )
    fun speed() = commandExecutor {
        requirePlayer { message("Sender must be a player") }
        val player = sender as Player
        val speed by doubleArg {
            parser {
                between(0.0, 10.0) { message("Speed must be between 0 and 10") }
            }
            invalid { message("Speed $it is not a valid speed") }
            missing {
                message("Fly Speed = ${player.flySpeed * 10}")
                message("Walk Speed = ${player.walkSpeed * 10}")
            }
        }
        then { message(speedExecutor(player, speed / 10)) }
    }

    private fun speedExecutor(sender: Player, speed: Double): String {
        sender.flySpeed = speed.toFloat()
        sender.walkSpeed = speed.toFloat()
        return "Speed set to $speed"
    }
}
