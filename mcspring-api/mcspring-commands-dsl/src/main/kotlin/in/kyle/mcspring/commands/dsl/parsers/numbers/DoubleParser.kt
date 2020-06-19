package `in`.kyle.mcspring.commands.dsl.parsers.numbers

import `in`.kyle.mcspring.commands.dsl.CommandContext

class DoubleParser(context: CommandContext, stringArg: String) : NumberParser<Double>(context, stringArg) {
    override fun zero() = 0.0
    override fun toNumber(s: String): Double? = s.toDoubleOrNull()
    override fun parse(s: String) = s.toDoubleOrNull()
}
