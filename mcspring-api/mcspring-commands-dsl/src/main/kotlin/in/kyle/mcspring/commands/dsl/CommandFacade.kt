package `in`.kyle.mcspring.commands.dsl

fun commandMeta(lambda: CommandMeta.() -> Unit) = CommandMeta().apply { lambda(this) }


infix fun CommandMeta.commandExecutor(lambda: CommandBuilder.() -> Unit): CommandMeta {
    executor = command(lambda)
    return this
}

fun command(lambda: CommandBuilder.() -> Unit) =
        CommandExecutor { context: CommandContext ->
            CommandBuilder(context).let {
                it.lambda()
                it.build()
            }
        }
