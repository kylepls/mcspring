package `in`.kyle.mcspring.commands.dsl

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

@DslMarker
annotation class CommandParserBuilder

class CommandMeta {
    lateinit var name: String
    var description: String = ""
    var usageMessage: String = ""
    var permission: String = ""
    var permissionMessage: String = ""
    var aliases: List<String> = listOf()
    lateinit var executor: CommandExecutor

    fun preRegister() {
        require(this::name.isInitialized) {"Command name not set"}
        require(this::executor.isInitialized) {"Command executor not set for command $name"}
    }
}

infix fun CommandMeta.executor(lambda: CommandBuilder.() -> Unit): CommandMeta {
    executor = commandExecutor(lambda)
    return this
}

fun command(lambda: CommandMeta.() -> Unit) = CommandMeta().apply { lambda(this) }

data class CommandContext(
        val sender: CommandSender,
        val label: String,
        val args: List<String>,
        val tabCompletions: MutableList<String> = mutableListOf(),
        internal var argIndex: Int = 0,
        internal val runExecutors: Boolean = true
) {
    fun nextArg() = args[argIndex]
}

data class CommandExecutor(val provider: (CommandContext) -> ParsedCommand)

fun commandExecutor(lambda: CommandBuilder.() -> Unit) =
        CommandExecutor { context: CommandContext ->
            CommandBuilder(context).let {
                try {
                    it.lambda()
                } catch (e: ContextReciever.BreakParseException) {
                }
                it.build()
            }
        }

@CommandParserBuilder
class CommandBuilder(context: CommandContext) : ContextReciever(context) {

    private val parsedArgs: MutableList<ValueArg<*>> = mutableListOf()

    fun stringArg(lambda: ValueBuilder<String, StringParser>.() -> Unit = {}) = valueArg(lambda) { StringParser(context, it) }
    fun intArg(lambda: ValueBuilder<Int, IntParser>.() -> Unit) = valueArg(lambda) { IntParser(context, it) }
    fun doubleArg(lambda: ValueBuilder<Double, DoubleParser>.() -> Unit) = valueArg(lambda) { DoubleParser(context, it) }
    fun booleanArg(lambda: ValueBuilder<Boolean, BooleanParser>.() -> Unit) = valueArg(lambda) { BooleanParser(context, it) }
    fun playerArg(lambda: ValueBuilder<Player, PlayerParser>.() -> Unit = {}) = valueArg(lambda) { PlayerParser(context, it) }

    fun <R> mapArg(lambda: ValueBuilder<R, MapParser<R>>.() -> Unit) = valueArg(lambda) { MapParser(context, it) }

    fun subcommand(lambda: SubcommandBuilder.() -> Unit) {
        arg(SubcommandBuilder(context), lambda)
    }

    fun require(predicate: () -> Boolean, lambda: ContextReciever.() -> Unit = {}) = require(predicate(), lambda)

    fun require(value: Boolean, lambda: ContextReciever.() -> Unit) {
        if (!value) {
            lambda(this)
            complete()
        }
    }

    fun requirePlayer(lambda: ContextReciever.() -> Unit) = require({ sender is Player }, lambda)

    fun requireConsole(lambda: ContextReciever.() -> Unit) = require({ sender is ConsoleCommandSender }, lambda)

    private fun <R, P : BaseParser<R>> valueArg(
            lambda: ValueBuilder<R, P>.() -> Unit,
            parserSupplier: (String) -> P
    ) = arg(ValueBuilder(context, parserSupplier), lambda)

    private fun <R, T : ArgBuilder<R>> arg(builder: T, lambda: T.() -> Unit): Lazy<R> {
        context.tabCompletions.clear()
        val arg = builder.apply(lambda).build()
        parsedArgs.add(arg)
        context.argIndex++
        return lazyOf(arg.returnValue)
    }

    fun then(lambda: ContextReciever.() -> Unit) {
        if (context.runExecutors) {
            lambda(this)
            complete()
        }
    }

    fun build(): ParsedCommand = ParsedCommand(parsedArgs)
}

@CommandParserBuilder
abstract class ArgBuilder<R>(
        context: CommandContext
) : ContextReciever(context) {

    internal open var returnValue: R? = null

    open fun invalid(lambda: ContextReciever.(arg: String) -> Unit) {
        if (returnValue == null && context.runExecutors) {
            lambda(context.nextArg())
            complete()
        }
    }

    abstract fun build(): ValueArg<R>
}

class ValueBuilder<R, out T : BaseParser<R>>(
        context: CommandContext,
        private val parserSupplier: (String) -> T
) : ArgBuilder<R>(context) {

    private var hasRunParser = false

    fun default(lambda: ContextReciever.() -> R?) {
        if (returnValue == null) {
            returnValue = lambda()
        }
    }

    fun parser(lambda: T.() -> Unit) {
        hasRunParser = true
        if (returnValue == null) {
            val parser = parserSupplier(context.nextArg())
            parser.lambda()
            returnValue = parser.returnValue
        }
    }

    fun missing(lambda: ContextReciever.() -> Unit) {
        if (!hasNextArg() && context.runExecutors) {
            lambda()
            complete()
        }
    }

    override fun invalid(lambda: ContextReciever.(arg: String) -> Unit) {
        runBaseParse()
        super.invalid(lambda)
    }

    private fun runBaseParse() {
        if (!hasRunParser) {
            parser {}
        }
    }

    override fun build(): ValueArg<R> {
        runBaseParse()
        if (returnValue != null) {
            return ValueArg(returnValue!!)
        } else {
            error("error parsing $context")
        }
    }
}

class SubcommandBuilder(context: CommandContext) : ArgBuilder<String>(context) {

    private val subCommands: MutableMap<List<String>, ParsedCommand> = mutableMapOf()

    fun on(vararg values: String, commandExecutor: CommandExecutor) = on({ it in values }, values.toList(), commandExecutor)
    fun on(vararg values: String, command: CommandBuilder.() -> Unit) {
        on({ it in values }, values.toList(), commandExecutor(command))
    }

    fun on(
            predicate: (String) -> Boolean,
            tabCompletions: Iterable<String>,
            commandExecutor: CommandExecutor
    ) {
        context.tabCompletions.addAll(tabCompletions)
        val argString = context.nextArg()
        if (predicate(argString)) {
            returnValue = argString
            context.argIndex++
            subCommands[tabCompletions.toList()] = commandExecutor.provider(context)
            complete()
        } else {
            val fakeContext = context.copy(runExecutors = false)
            val parsed = commandExecutor.provider(fakeContext)
            subCommands[tabCompletions.toList()] = parsed
        }
    }

    fun missing(lambda: SubcommandMissingBuilder.() -> Unit) {
        if (!hasNextArg() && context.runExecutors) {
            lambda(SubcommandMissingBuilder(subCommands, context))
            complete()
        }
    }

    override fun build() = SubCommandArg(subCommands, returnValue!!)

    class SubcommandMissingBuilder(
            val subCommands: Map<List<String>, ParsedCommand>,
            context: CommandContext
    ) : ContextReciever(context)
}
