package `in`.kyle.mcspring.commands.dsl.parsers.numbers

import `in`.kyle.mcspring.commands.dsl.parsers.BaseParser
import `in`.kyle.mcspring.commands.dsl.CommandContext
import `in`.kyle.mcspring.commands.dsl.parsers.ConditionFail

abstract class NumberParser<T>(context: CommandContext, stringArg: String) : BaseParser<T>(context, stringArg)
        where T : Number,
              T : Comparable<T> {

    init {
        returnValue = parse(stringArg)
    }

    internal abstract fun toNumber(s: String): T?
    internal abstract fun zero(): T
    internal abstract fun parse(s: String): T?

    fun between(lower: T, upper: T, conditionFail: ConditionFail) = require(predicate = { it > lower && it < upper }, conditionFail = conditionFail)
    fun greaterThan(value: T, conditionFail: ConditionFail) = require(predicate = { it > value }, conditionFail = conditionFail)
    fun greaterThanEqual(value: T, conditionFail: ConditionFail) = require(predicate = { it >= value }, conditionFail = conditionFail)
    fun lessThan(value: T, conditionFail: ConditionFail) = require(predicate = { it < value }, conditionFail = conditionFail)
    fun lessThanEqual(value: T, conditionFail: ConditionFail) = require(predicate = { it <= value }, conditionFail = conditionFail)
    fun positive(conditionFail: ConditionFail) = require(predicate = { it > zero() }, conditionFail = conditionFail)
    fun negative(conditionFail: ConditionFail) = require(predicate = { it < zero() }, conditionFail = conditionFail)
    fun nonZero(conditionFail: ConditionFail) = require(predicate = { it != zero() }, conditionFail = conditionFail)
}
