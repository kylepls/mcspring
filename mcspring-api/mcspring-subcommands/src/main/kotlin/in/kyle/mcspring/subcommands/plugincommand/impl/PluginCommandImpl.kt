package `in`.kyle.mcspring.subcommands.plugincommand.impl

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandBase.CompletionStage
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.impl.javasupport.PluginCommandExecutorsJavaSupport
import `in`.kyle.mcspring.subcommands.plugincommand.impl.javasupport.PluginCommandWithJavaSupport
import org.bukkit.command.CommandSender

class PluginCommandImpl(
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

    override fun dirtiesState(requiredStates: Array<State>,
                              resultingState: State,
                              action: () -> Unit) {
        if (state in requiredStates) {
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

