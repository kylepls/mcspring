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

    fun tabCompletions(vararg strings: String) = context.tabCompletions.addAll(strings)
    fun tabCompletions(strings: List<String>) = context.tabCompletions.addAll(strings)

    fun hasNextArg() = context.argIndex < context.args.size

    fun commandMissing() {
        throw BreakParseException.ParseMissingException()
    }

    fun commandInvalid() {
        throw BreakParseException.ParseInvalidException()
    }

    fun commandComplete() {
        throw BreakParseException.ParseCompletedException()
    }

    sealed class BreakParseException : RuntimeException() {
        class ParseCompletedException : BreakParseException()
        class ParseMissingException : BreakParseException()
        class ParseInvalidException : BreakParseException()
    }
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


