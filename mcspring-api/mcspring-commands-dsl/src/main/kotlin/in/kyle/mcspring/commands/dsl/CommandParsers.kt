package `in`.kyle.mcspring.commands.dsl

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class RunInvalidBlock : RuntimeException()

typealias Error = (String) -> Unit

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
            error: Error
    ) {
        context.tabCompletions.addAll(hints)
        if (returnValue != null && !predicate(returnValue!!)) {
            returnValue = null
            throw RunInvalidBlock()
        }
        if (stringArg != null && returnValue == null && context.runExecutors) {
            error(stringArg)
            complete()
        }
    }
}

class MapParser<R>(context: CommandContext, stringArg: String) : BaseParser<R>(context, stringArg) {
    fun map(vararg pairs: Pair<String, R>) = map(mapOf(*pairs))

    fun map(map: Map<String, R>) = mapped(map.keys) { map[it] }
}

class StringParser(context: CommandContext, stringArg: String) : BaseParser<String>(context, stringArg) {
    init {
        returnValue = stringArg
    }

    fun anyOf(vararg string: String, error: Error) = anyOf(string.toList(), error)
    fun anyOf(strings: List<String>, error: Error) = require(strings, { it in strings }, error)
    fun anyString() = mapped { it }
    fun anyStringMatching(regex: Regex, error: Error) = require(predicate = { it.matches(regex) }, error = error)
    fun anyInt(error: Error) = require(predicate = { it.toIntOrNull() != null }, error = error)
    fun anyDouble(error: Error) = require(predicate = { it.toDoubleOrNull() != null }, error = error)
    fun anyFloat(error: Error) = require(predicate = { it.toFloatOrNull() != null }, error = error)
    fun anyByte(error: Error) = require(predicate = { it.toByteOrNull() != null }, error = error)
    fun anyShort(error: Error) = require(predicate = { it.toShortOrNull() != null }, error = error)
    fun anyBoolean(error: Error) = anyOf("true", "false", error = error)
}

abstract class NumberParser<T>(context: CommandContext, stringArg: String) : BaseParser<T>(context, stringArg)
        where T : Number,
              T : Comparable<T> {

    init {
        returnValue = parse(stringArg)
    }

    internal abstract fun toNumber(s: String): T?
    internal abstract fun zero(): T
    internal abstract fun parse(s: String): T?

    fun between(lower: T, upper: T, error: Error) = require(predicate = { it > lower && it < upper }, error = error)
    fun greaterThan(value: T, error: Error) = require(predicate = { it > value }, error = error)
    fun greaterThanEqual(value: T, error: Error) = require(predicate = { it >= value }, error = error)
    fun lessThan(value: T, error: Error) = require(predicate = { it < value }, error = error)
    fun lessThanEqual(value: T, error: Error) = require(predicate = { it <= value }, error = error)
    fun positive(error: Error) = require(predicate = { it > zero() }, error = error)
    fun nonZero(value: T, error: Error) = require(predicate = { it != zero() }, error = error)
}

class IntParser(context: CommandContext, stringArg: String) : NumberParser<Int>(context, stringArg) {
    override fun zero() = 0
    override fun toNumber(s: String): Int? = s.toIntOrNull()
    fun even(error: Error) = require(predicate = { it % 2 == 0 && it != 0 }, error = error)
    fun odd(error: Error) = require(predicate = { it % 2 == 1 && it != 0 }, error = error)
    fun between(range: IntRange, error: Error) = require(predicate = { it in range }, error = error)
    override fun parse(s: String) = s.toIntOrNull()
}

class DoubleParser(context: CommandContext, stringArg: String) : NumberParser<Double>(context, stringArg) {
    override fun zero() = 0.0
    override fun toNumber(s: String): Double? = s.toDoubleOrNull()
    override fun parse(s: String) = s.toDoubleOrNull()
}

class BooleanParser(context: CommandContext, stringArg: String) : BaseParser<Boolean>(context, stringArg) {
    init {
        val booleans = listOf("true", "false")
        context.tabCompletions.addAll(booleans)
        returnValue = stringArg.toBoolean().takeIf { stringArg in booleans }
    }
}

class PlayerParser(
        context: CommandContext,
        stringArg: String
) : BaseParser<Player>(context, stringArg) {
    init {
        val players = Bukkit.getOnlinePlayers()
        context.tabCompletions.addAll(players.map { it.name })
        returnValue = Bukkit.getPlayer(stringArg)
    }
}
