package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandContext

class MapParser<R>(context: CommandContext, stringArg: String) : BaseParser<R>(context, stringArg) {
    fun map(vararg pairs: Pair<String, R>) = map(mapOf(*pairs))

    fun map(map: Map<String, R>) = mapped(map.keys) { map[it] }
}
