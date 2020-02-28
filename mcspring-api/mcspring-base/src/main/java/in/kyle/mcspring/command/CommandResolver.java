package in.kyle.mcspring.command;

import org.bukkit.command.CommandSender;

import lombok.Value;

public interface CommandResolver {
    
    Resolver makeResolver(Command command);
    
    @Value
    class Command {
        private final CommandSender sender;
        private final String[] args;
        private final String label;
    }
}
