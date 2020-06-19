package `in`.kyle.mcspring.commands.dsl.parsers

import `in`.kyle.mcspring.commands.dsl.CommandContext
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
