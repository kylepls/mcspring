package in.kyle.mcspring.manager.commands;

import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.subcommands.PluginCommand;

@Component
class CommandHeal {
    
    @Command(value = "heal",
             description = "Heal yourself or another player",
             usage = "/heal <player>?")
    void heal(PluginCommand command) {
        command.withPlayerSender("Sender must be a player");
        command.withPlayer(s -> String.format("Player %s not found", s));
        command.then(this::executeHeal);
        command.otherwise(this::executeHeal);
    }
    
    String executeHeal(Player target) {
        target.setHealth(target.getMaxHealth());
        return String.format("Healed %s", target.getName());
    }
}
