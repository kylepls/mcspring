package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandContext

class StringParser(context: CommandContext, stringArg: String?) : BaseParser<String>(context, stringArg) {
    init {
        returnValue = stringArg
    }

    fun anyOf(vararg string: String, conditionFail: ConditionFail) = anyOf(string.toList(), conditionFail)
    fun anyOf(strings: List<String>, conditionFail: ConditionFail) = require(strings, { it in strings }, conditionFail)
    fun anyString() = mapped { it }
    fun anyStringMatching(regex: Regex, conditionFail: ConditionFail) = require(predicate = { it.matches(regex) }, conditionFail = conditionFail)
    fun anyInt(conditionFail: ConditionFail) = require(predicate = { it.toIntOrNull() != null }, conditionFail = conditionFail)
    fun anyDouble(conditionFail: ConditionFail) = require(predicate = { it.toDoubleOrNull() != null }, conditionFail = conditionFail)
    fun anyFloat(conditionFail: ConditionFail) = require(predicate = { it.toFloatOrNull() != null }, conditionFail = conditionFail)
    fun anyByte(conditionFail: ConditionFail) = require(predicate = { it.toByteOrNull() != null }, conditionFail = conditionFail)
    fun anyShort(conditionFail: ConditionFail) = require(predicate = { it.toShortOrNull() != null }, conditionFail = conditionFail)
    fun anyBoolean(conditionFail: ConditionFail) = anyOf("true", "false", conditionFail = conditionFail)
}
