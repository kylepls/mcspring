package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandImpl
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
internal class CommandHeal {
    @Command(value = "heal", description = "Heal yourself or another player", usage = "/heal <player>?")
    fun heal(command: PluginCommandImpl) {
        command.requiresPlayerSender { "Sender must be a player" }
        command.withPlayer { "Player $it not found" }
        command.then(this::executeHeal)
        command.otherwise(this::executeHeal)
    }

    private fun executeHeal(target: Player): String {
        target.health = target.maxHealth
        return "Healed ${target.name}"
    }
}
