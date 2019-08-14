package in.kyle.mcspring.manager.commands;

import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.subcommands.PluginCommand;

@Component
class CommandOp {
    
    @Command(value = "op",
             description = "Toggle yourself or another players OP status",
             usage = "/op <player>?")
    void op(PluginCommand command) {
        command.withPlayer(s -> String.format("Player %s not found", s));
        command.then(this::toggleOp);
        
        command.otherwise(this::toggleOp);
    }
    
    String toggleOp(CommandSender target) {
        target.setOp(!target.isOp());
        return String.format("%s is %s op", target.getName(), target.isOp() ? "now" : "no longer");
    }
}
