package test.other.stats

import lombok.RequiredArgsConstructor
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.example.factions.api.FactionsApi
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
@PluginDepend(plugins = "factions")
internal class FactionsStats {
    private val factionsApi: FactionsApi? = null

    @Command("stats")
    fun test(): String {
//        return "test";
        return String.format("There are %d factions", factionsApi!!.factions.size)
    }

    @EventHandler
    fun move(e: PlayerMoveEvent) {
        println(e.getPlayer().getName().toString() + " moved")
    }
}
