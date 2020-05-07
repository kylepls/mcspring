package `in`.kyle.mcspring.subcommands.plugincommand

import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

interface PluginCommandRequires : PluginCommandBase {

    fun requires(predicate: Boolean, errorMessage: () -> String) {
        if (!predicate) {
            dirtiesState { sendMessage(errorMessage()) }
        }
    }

    fun requiresPlayerSender(errorMessage: () -> String) {
        requires(sender is Player, errorMessage)
    }

    fun requireConsoleSender(errorMessage: () -> String) {
        requires(sender is ConsoleCommandSender, errorMessage)
    }

    fun requiresPermission(permission: String, errorMessage: () -> String) {
        requires(sender.hasPermission(permission), errorMessage)
    }

    fun requiresOp(errorMessage: () -> String) = requires(sender.isOp, errorMessage)

}
