package `in`.kyle.mcspring.subcommands.tab

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandImpl
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.bukkit.command.CommandSender
import org.springframework.stereotype.Component

@Component
class TabDiscovery(
        private val injection: SimpleMethodInjection
) {
    fun getCompletions(sender: CommandSender,
                       commandString: String,
                       consumer: (PluginCommand) -> Unit): List<String> {
        val (parts, prefix) = getCommandParts(commandString)

        val command = PluginCommandImpl(injection, sender, parts, false)
        consumer(command)

        val completionCandidates = getCompletions(command)
        return completionCandidates.filter { it.startsWith(prefix) }
    }

    private fun getCommandParts(command: String): Pair<MutableList<String>, String> {
        val parts = command.split(" ").filter { it.isNotBlank() }.toMutableList()

        val prefix = if (!command.endsWith(" ") && command.isNotBlank()) {
            parts.removeAt(parts.size - 1)
        } else {
            ""
        }
        return Pair(parts, prefix)
    }

    private fun getCompletions(command: PluginCommandImpl): List<String> {
        return if (command.child != null) {
            getCompletions(command.child as PluginCommandImpl)
        } else if (command.state == State.MISSING_ARG || command.state == State.CLEAN) {
            command.completions.flatMap { it.completions }
        } else {
            emptyList()
        }
    }
}
