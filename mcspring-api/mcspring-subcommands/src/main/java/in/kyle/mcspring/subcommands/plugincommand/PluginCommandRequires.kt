package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

interface PluginCommandRequires : PluginCommandBase, PluginCommand {

    override fun requires(predicate: Boolean, errorMessage: () -> String) {
        if (!predicate) {
            dirtiesState { sendMessage(errorMessage()) }
        }
    }

    override fun requiresPlayerSender(errorMessage: () -> String) {
        requires(sender is Player, errorMessage)
    }

    override fun requiresConsoleSender(errorMessage: () -> String) {
        requires(sender is ConsoleCommandSender, errorMessage)
    }

    override fun requiresPermission(permission: String, errorMessage: () -> String) {
        requires(sender.hasPermission(permission), errorMessage)
    }

    override fun requiresOp(errorMessage: () -> String) = requires(sender.isOp, errorMessage)

}
