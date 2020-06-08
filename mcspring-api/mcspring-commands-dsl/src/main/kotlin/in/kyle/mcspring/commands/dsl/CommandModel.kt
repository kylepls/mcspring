package `in`.kyle.mcspring.commands.dsl

import org.bukkit.ChatColor

open class ContextReciever(val context: CommandContext) {
    val sender = context.sender
    val label = context.label
    val args = context.args

    fun message(message: String) {
        if (context.runExecutors) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
        }
    }

    fun hasNextArg() = context.argIndex < context.args.size

    fun complete(): Nothing = throw BreakParseException()

    class BreakParseException : RuntimeException()
}

data class ParsedCommand(
        val args: List<ValueArg<*>>
)

open class ValueArg<R>(
        val returnValue: R
)

class SubCommandArg(
        val subCommands: Map<List<String>, ParsedCommand>,
        returnValue: String
): ValueArg<String>(returnValue)


