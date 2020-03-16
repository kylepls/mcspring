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
    
    @Command(value = "faction", aliases = "f", description = "Faction management commands")
    void commandFaction(PluginCommand command) {
        command.withPlayerSender("Sender must be a player");
        command.on("create", this::commandCreate);
        command.on("delete", this::commandDelete);
        command.on("list", this::executeFactionList);
        command.on("join", this::commandJoin);
        command.on("info", this::executeFactionInfo);
        command.onInvalid(s -> String.format("Invalid sub-command %s", s));
        command.otherwise("Usage: /faction <create|delete|join|list|mine>");
    }
    
    private void commandCreate(PluginCommand command) {
        command.withString();
        command.then(this::executeFactionCreate);
        command.otherwise("Usage: /faction create <name>");
    }
    
    private void commandDelete(PluginCommand command) {
        command.withAny(factions.getFactionNames(),
                        s -> String.format("Faction &d%s&r not found", s));
        command.then(this::executeFactionDelete);
        command.onInvalid(s -> String.format("Invalid sub-command: %s", s));
        command.otherwise("Usage: /faction delete <name>");
    }
    
    private void commandJoin(PluginCommand command) {
        command.withMap(factions.getFactions()
                                .stream()
                                .collect(Collectors.toMap(Faction::getName, Function.identity())),
                        s -> String.format("Faction %s not found", s));
        command.then(this::executeFactionJoin);
        command.otherwise("Usage: /faction join <name>");
    }
    
    
    private String executeFactionJoin(Player sender, Faction faction) {
        if (!factions.isFactionMember(sender)) {
            faction.getMembers().put(sender.getUniqueId(), Faction.Rank.MEMBER);
            return String.format("You joined &1%s", faction.getName());
        } else {
            return "You must leave your current faction before joining another one";
        }
    }
    
    private String executeFactionInfo(Player sender) {
        return factions.getFaction(sender)
                .map(f -> "Faction: " + formatFaction(f))
                .orElse("You are not in a faction");
    }
    
    private String executeFactionList() {
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
    
    private String executeFactionCreate(Player sender, String name) {
        if (factions.isFactionMember(sender)) {
            return "You must first leave your current faction";
        } else {
            factions.addFaction(new Faction(name, sender.getUniqueId()));
            return String.format("Created faction: &1%s", name);
        }
    }
    
    private String executeFactionDelete(Player sender, String name) {
        factions.removeFaction(name);
        return String.format("Deleted faction: &1%s", name);
    }
}
