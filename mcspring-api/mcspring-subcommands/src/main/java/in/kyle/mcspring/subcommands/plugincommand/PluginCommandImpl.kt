package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.CompletionStage
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.javacompat.PluginCommandExecutorsJavaSupport
import `in`.kyle.mcspring.subcommands.plugincommand.javacompat.PluginCommandWithJavaSupport
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.lang.RuntimeException

open class PluginCommandImpl(
        override val injection: SimpleMethodInjection,
        override val sender: CommandSender,
        override val parts: MutableList<String>,
        override val runExecutors: Boolean
) : PluginCommandBase, PluginCommandWith, PluginCommandRequires, PluginCommandExecutors,
        PluginCommandExecutorsJavaSupport, PluginCommandWithJavaSupport {

    override val injections = mutableListOf<Any>()
    override var state: State = State.CLEAN
    override var child: PluginCommandBase? = null
    override val completions: MutableList<CompletionStage> = mutableListOf()

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

    override fun nextPart(): String? {
        val part = parts.getOrNull(0).takeIf { state == State.CLEAN }
        assert(part?.isNotBlank() ?: true)
        return part
    }
}

