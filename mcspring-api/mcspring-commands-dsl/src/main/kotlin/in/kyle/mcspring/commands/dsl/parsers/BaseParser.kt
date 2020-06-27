package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandContext
import `in`.kyle.mcspring.commands.dsl.CommandParserBuilder
import `in`.kyle.mcspring.commands.dsl.ContextReciever

class RunInvalidBlock : RuntimeException()

typealias ConditionFail = (String) -> Unit

@CommandParserBuilder
abstract class BaseParser<R>(
        context: CommandContext,
        private val stringArg: String?
) : ContextReciever(context) {

    internal var returnValue: R? = null

    protected fun mapped(hints: Iterable<String> = emptyList(), mapper: (String) -> R?) {
        context.tabCompletions.addAll(hints)
        if (returnValue == null) {
            returnValue = stringArg?.let { mapper(it) }
        }
    }

    protected fun require(
            hints: List<String> = emptyList(),
            predicate: (R) -> Boolean,
            conditionFail: ConditionFail = {}
    ) {
        context.tabCompletions.addAll(hints)
        val condition = predicate(returnValue!!)
        if (returnValue != null && !condition) {
            if (conditionFail == {}) {
                returnValue = null
                throw RunInvalidBlock()
            } else if (context.runExecutors) {
                conditionFail(stringArg!!)
                commandInvalid()
            }
        }
    }
}

