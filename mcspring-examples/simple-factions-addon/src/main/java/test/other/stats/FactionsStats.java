package test.other.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.processor.annotation.PluginDepend;
import in.kyle.mcspring.processor.annotation.SpringPlugin;

import org.example.factions.api.FactionsApi;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@PluginDepend(plugins = "factions")
@SpringPlugin(name = "faction-stats", description = "Shows stats for factions")
class FactionsStats {

    private final FactionsApi factionsApi;
    
    @Command("stats")
    String test() {
//        return "test";
        return String.format("There are %d factions", factionsApi.getFactions().size());
    }
    
    @EventHandler
    void move(PlayerMoveEvent e) {
        System.out.println(e.getPlayer().getName() + " moved");
    }
}
