package `in`.kyle.mcspring.commands.dsl

fun command(lambda: CommandMeta.() -> Unit) = CommandMeta().apply { lambda(this) }

infix fun CommandMeta.executor(lambda: CommandBuilder.() -> Unit): CommandMeta {
    executor = commandExecutor(lambda)
    return this
}

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
