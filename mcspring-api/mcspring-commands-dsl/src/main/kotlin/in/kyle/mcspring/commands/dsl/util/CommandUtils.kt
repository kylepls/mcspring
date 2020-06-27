package `in`.kyle.mcspring.commands.dsl.util

import `in`.kyle.mcspring.commands.dsl.CommandContext
import `in`.kyle.mcspring.commands.dsl.CommandMeta
import `in`.kyle.mcspring.commands.dsl.ContextReciever
import `in`.kyle.mcspring.commands.dsl.ContextReciever.*
import `in`.kyle.mcspring.commands.dsl.ContextReciever.BreakParseException.*
import `in`.kyle.mcspring.commands.dsl.ParsedCommand
import org.bukkit.command.Command
import org.bukkit.command.CommandException
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class NoValidExecutorException(string: String) : CommandException(string)

object CommandUtils {

    private val noValidExecutorException = NoValidExecutorException("""
                It appears that this command executed without hitting a terminal block.
                A terminal block is an endpoint for a command. Such would be `missing`, `invalid`,
                or `then`. Make sure your command executes one of these blocks.
            """.trimIndent())

    fun register(plugin: Plugin, meta: CommandMeta) {
        meta.validate()
        CommandMapWrapper.registerCommand(plugin, makeBukkitCommand(meta).apply {
            aliases = meta.aliases
            description = meta.description
            usage = meta.usageMessage
            meta.permission.takeIf { it.isNotBlank() }
                    ?.run { permission = meta.permission }
            meta.permissionMessage.takeIf { it.isNotBlank() }
                    ?.run { permissionMessage = meta.permissionMessage }
        })
    }

    fun runCommand(context: CommandContext, provider: (CommandContext) -> ParsedCommand) {
        try {
            provider(context)
            throw noValidExecutorException
        } catch (_: BreakParseException) {
        }
    }

    fun getCompletions(context: CommandContext, provider: (CommandContext) -> ParsedCommand): List<String> {
        val completions = try {
            provider(context)
            throw noValidExecutorException
        } catch (e: BreakParseException) {
            when (e) {
                is ParseCompletedException -> listOf<String>()
                else -> context.tabCompletions
            }
        }

        // make sure this is the last arg
        return if (context.argIndex+1 == context.args.size || context.args.isEmpty()) {
            val arg = context.nextArg ?: ""
            completions.filter { it.toLowerCase().startsWith(arg.toLowerCase()) }
        } else {
            emptyList()
        }
    }

    private fun makeBukkitCommand(meta: CommandMeta) = object : Command(meta.name) {
        override fun execute(
                sender: CommandSender,
                commandLabel: String,
                args: Array<String>
        ): Boolean {
            val context = CommandContext(sender, label, args.toList())
            val provider = meta.executor.provider
            runCommand(context, provider)
            return true
        }

        override fun tabComplete(
                sender: CommandSender,
                alias: String,
                args: Array<String>
        ): List<String> {
            val provider = meta.executor.provider
            val context = CommandContext(sender, label, args.toList(), runExecutors = false)
            return getCompletions(context, provider)
        }
    }
}
