package org.example.factions.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

//@SpringPlugin(name = "factions", description = "A simple factions plugin")
public interface FactionsApi {
    void addFaction(Faction faction);
    
    void removeFaction(String factionName);
    
    boolean isFactionMember(Player player);
    
    Optional<Faction> getFaction(Player player);
    
    java.util.Set<Faction> getFactions();
    
    List<String> getFactionNames();
}
