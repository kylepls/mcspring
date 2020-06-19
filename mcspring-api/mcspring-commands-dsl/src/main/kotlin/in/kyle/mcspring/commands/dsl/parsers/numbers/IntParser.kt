package `in`.kyle.mcspring.commands.dsl.parsers.numbers

import `in`.kyle.mcspring.commands.dsl.CommandContext
import `in`.kyle.mcspring.commands.dsl.parsers.ConditionFail

class IntParser(context: CommandContext, stringArg: String) : NumberParser<Int>(context, stringArg) {
    override fun zero() = 0
    override fun toNumber(s: String): Int? = s.toIntOrNull()
    fun even(conditionFail: ConditionFail) = require(predicate = { it % 2 == 0 && it != 0 }, conditionFail = conditionFail)
    fun odd(conditionFail: ConditionFail) = require(predicate = { it % 2 == 1 && it != 0 }, conditionFail = conditionFail)
    fun between(range: IntRange, conditionFail: ConditionFail) = require(predicate = { it in range }, conditionFail = conditionFail)
    override fun parse(s: String) = s.toIntOrNull()
}
