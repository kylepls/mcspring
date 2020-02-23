package in.kyle.mcspring.testplugin;

import org.bukkit.Bukkit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;

@Component
public class TestBroadcaster {
    
    private boolean enabled = false;
    
    @Scheduled(fixedDelay = 10000)
    public void interval() {
        if (enabled) {
            Bukkit.broadcastMessage("REMEMBER TO DONATE");
        }
    }
    
    @Command("toggle-broadcast")
    public String toggle() {
        enabled = !enabled;
        return String.format("Messages %s", enabled ? "enabled" : "disabled");
    }
}
