package in.kyle.mcspring.testplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TestEvent {
    
    private final Logger logger;
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        logger.info(e.getPlayer().getName() + " moved");
    }
}
