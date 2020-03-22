package org.example.factions.api;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Faction {
    
    private final Map<UUID, Rank> members = new HashMap<>();
    private String name;
    
    public Faction(String name, UUID owner) {
        this.name = name;
        this.members.put(owner, Rank.OWNER);
    }
    
    public boolean isMember(Player player) {
        return members.containsKey(player.getUniqueId());
    }
    
    public enum Rank {
        OWNER,
        MEMBER
    }
}
