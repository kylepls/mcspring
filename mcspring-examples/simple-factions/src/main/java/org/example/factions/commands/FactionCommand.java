package org.example.factions.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.example.factions.api.Faction;
import org.example.factions.api.FactionsApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.subcommands.PluginCommand;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class FactionCommand {
    
    private final FactionsApi factions;
    
    @Command("faction")
    void faction(PluginCommand command) {
        command.on("create", this::create);
        command.on("delete", this::delete);
        command.on("list", this::list);
        command.on("join", this::join);
        command.on("mine", this::info);
        command.onInvalid(s -> String.format("Invalid sub-command %s", s));
        command.otherwise("Usage: /faction <create|delete|join|list>");
    }
    
    private void info(PluginCommand command) {
        command.then(this::factionMine);
    }
    
    private void join(PluginCommand command) {
        command.withMap(factions.getFactions()
                                .stream()
                                .collect(Collectors.toMap(Faction::getName, Function.identity())),
                        s -> String.format("Faction %s not found", s));
        command.then(this::factionJoin);
        command.otherwise("Usage: /faction join <name>");
    }
    
    private void create(PluginCommand command) {
        command.withString();
        command.then(this::factionCreate);
        command.otherwise("Usage: /faction create <name>");
    }
    
    private void delete(PluginCommand command) {
        command.withAny(factions.getFactionNames(),
                        s -> String.format("Faction &d%s&r not found", s));
        command.then(this::factionDelete);
        command.onInvalid(s -> String.format("Invalid sub-command: %s", s));
        command.otherwise("Usage: /faction delete <name>");
    }
    
    private void list(PluginCommand command) {
        command.then(this::factionList);
    }
    
    private String factionJoin(Player sender, Faction faction) {
        if (!factions.isFactionMember(sender)) {
            faction.getMembers().put(sender.getUniqueId(), Faction.Rank.MEMBER);
            return String.format("You joined &1%s", faction.getName());
        } else {
            return "You must leave your current faction before joining another one";
        }
    }
    
    private String factionMine(Player sender) {
        return factions.getFaction(sender)
                .map(f -> "Faction: " + formatFaction(f))
                .orElse("You are not in a faction");
    }
    
    private String factionList() {
        return "Factions: \n" + factions.getFactions()
                .stream()
                .map(this::formatFaction)
                .collect(Collectors.joining("\n"));
    }
    
    private String formatFaction(Faction faction) {
        return String.format("%s: %s",
                             faction.getName(),
                             faction.getMembers()
                                     .entrySet()
                                     .stream()
                                     .map(e -> Bukkit.getPlayer(e.getKey()).getName() + "(" +
                                               e.getValue().name().toLowerCase() + ")")
                                     .collect(Collectors.joining(", ")));
    }
    
    private String factionCreate(Player sender, String name) {
        if (factions.isFactionMember(sender)) {
            return "You must first leave your current faction";
        } else {
            factions.addFaction(new Faction(name, sender.getUniqueId()));
            return String.format("Created faction: &1%s", name);
        }
    }
    
    private String factionDelete(Player sender, String name) {
        factions.removeFaction(name);
        return String.format("Deleted faction: &1%s", name);
    }
}
