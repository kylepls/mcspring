package com.joshb.testploogin;

import com.joshb.mcspringboot.SpringPlugin;
import com.joshb.mcspringboot.command.Command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.springframework.scheduling.annotation.Scheduled;

public class TestPloogin extends SpringPlugin {
    
    @Command("test")
    public String playerSender(Player sender, String command) {
        return command + " works!";
    }
    
    @Scheduled(fixedDelay = 10000)
    public void interval() {
        Bukkit.broadcastMessage("REMEMBER TO DONATE");
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        getLogger().info(e.getPlayer().getName() + " moved");
    }
}
