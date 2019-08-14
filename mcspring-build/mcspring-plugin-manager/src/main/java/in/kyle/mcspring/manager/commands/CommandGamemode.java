package in.kyle.mcspring.manager.commands;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.subcommands.PluginCommand;

@Component
class CommandGamemode {
    
    @Command(value = "gamemode",
             aliases = "gm",
             description = "Set your game mode",
             usage = "/gamemode <creative|survival...>")
    void gamemode(PluginCommand command) {
        command.withPlayerSender("Only players can run this command.");
        Map<String, GameMode> gamemodes = new HashMap<>();
        for (GameMode value : GameMode.values()) {
            gamemodes.put(value.name().toLowerCase(), value);
            gamemodes.put(String.valueOf(value.getValue()), value);
        }
        command.withMap(gamemodes, s -> String.format("%s is not a valid game mode", s));
        command.then(this::gamemodeExecutor);
        
        command.otherwise("Usage: /gamemode <game mode>");
    }
    
    @Command(value = "gmc", description = "Set your game mode to creative")
    String gmc(Player sender) {
        return gamemodeExecutor(sender, GameMode.CREATIVE);
    }
    
    @Command(value = "gms", description = "Set your game mode to survival")
    String gms(Player sender) {
        return gamemodeExecutor(sender, GameMode.SURVIVAL);
    }
    
    String gamemodeExecutor(Player sender, GameMode gameMode) {
        sender.setGameMode(gameMode);
        return String.format("Game mode set to %s", gameMode.name().toLowerCase());
    }
}
