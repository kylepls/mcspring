package in.kyle.mcspring.manager.commands;

import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.subcommands.PluginCommand;

@Component
class CommandSpeed {
    
    @Command(value = "speed",
             description = "Set your movement and fly speed",
             usage = "/speed <player> <speed>")
    void speed(PluginCommand command) {
        command.withPlayerSender("Sender must be a player");
        command.withDouble("Speed value must be an integer");
        command.then(this::speedExecutor);
        command.otherwise("Usage: /speed <value>");
    }
    
    String speedExecutor(Player sender, double speed) {
        sender.setFlySpeed((float) speed);
        sender.setWalkSpeed((float) speed);
        return String.format("Speed set to %f", speed);
    }
}
