package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.commands.dsl.command
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import `in`.kyle.mcspring.commands.dsl.mcspring.Command
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
internal class CommandHeal {

    @Command(
            value = "heal",
            description = "Heal yourself or another player",
            usage = "/heal <player>?"
    )
    fun heal() = command {
        requirePlayer { message("Sender must be a player") }

        val target = playerArg {
            default { sender as Player }
            invalid { message("Target player $it not found") }
        }

        val health = doubleArg {
            default { 20.0 }
            parser {
                between(0.0, 20.0) { message("Heal value must be between 0 and 20") }
            }
            invalid { message("Invalid health amount $it") }
        }

        then { message(executeHeal(target, health)) }
    }

    private fun executeHeal(target: Player, health: Double): String {
        @Suppress("DEPRECATION")
        target.health += health
        return "Healed ${target.name} by $health"
    }
}
