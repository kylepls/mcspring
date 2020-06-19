package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandContext


class BooleanParser(context: CommandContext, stringArg: String) : BaseParser<Boolean>(context, stringArg) {
    init {
        val booleans = listOf("true", "false")
        context.tabCompletions.addAll(booleans)

        val lowered = stringArg.toLowerCase()
        if (lowered in booleans) {
            returnValue = lowered.toBoolean()
        }
    }
}
