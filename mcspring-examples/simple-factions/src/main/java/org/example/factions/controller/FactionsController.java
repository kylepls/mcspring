package org.example.factions.controller;

import org.bukkit.entity.Player;
import org.example.factions.api.Faction;
import org.example.factions.api.FactionsApi;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

@Controller
class FactionsController implements FactionsApi {
    
    @Getter
    private final Set<Faction> factions = new HashSet<>();
    
    @Override
    public void addFaction(Faction faction) {
        factions.add(faction);
    }
    
    @Override
    public void removeFaction(String factionName) {
        factions.removeIf(f -> f.getName().equals(factionName));
    }
    
    @Override
    public boolean isFactionMember(Player player) {
        return getFaction(player).map(f -> f.getMembers().containsKey(player.getUniqueId()))
                .orElse(false);
    }
    
    @Override
    public Optional<Faction> getFaction(Player player) {
        return factions.stream().filter(f -> f.isMember(player)).findFirst();
    }
    
    @Override
    public List<String> getFactionNames() {
        return factions.stream().map(Faction::getName).collect(Collectors.toList());
    }
}
