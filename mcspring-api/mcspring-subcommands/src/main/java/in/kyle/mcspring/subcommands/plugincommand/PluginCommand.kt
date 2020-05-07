package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.javacompat.PluginCommandExecutorsJavaSupport
import `in`.kyle.mcspring.subcommands.plugincommand.javacompat.PluginCommandWithJavaSupport
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

open class PluginCommand(
        override val injection: SimpleMethodInjection,
        override val sender: CommandSender,
        override val parts: MutableList<String>
) : PluginCommandBase, PluginCommandWith, PluginCommandRequires, PluginCommandExecutors,
        PluginCommandExecutorsJavaSupport, PluginCommandWithJavaSupport {

    override val injections = mutableListOf<Any>()
    override var state: State = State.CLEAN

    override fun sendMessage(message: String) {
        if (message.isNotBlank()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
        }
    }

    override fun dirtiesState(predicate: Boolean,
                              requiredStates: Array<State>,
                              resultingState: State,
                              action: () -> Unit) {
        if (state in requiredStates && predicate) {
            action()
            state = resultingState
        }
    }

    override fun nextPart(): String? = parts.getOrNull(0).takeIf { state == State.CLEAN }

    override fun consumePart() {
        parts.removeAt(0)
    }
}
