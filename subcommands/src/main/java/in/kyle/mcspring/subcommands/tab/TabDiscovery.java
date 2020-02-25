package in.kyle.mcspring.subcommands.tab;

import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import in.kyle.mcspring.command.SimpleMethodInjection;
import in.kyle.mcspring.subcommands.PluginCommand;
import in.kyle.mcspring.subcommands.PluginCommandTabCompletable;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Component
@RequiredArgsConstructor
public class TabDiscovery {
    
    private final SimpleMethodInjection injection;
    
    public List<String> getCompletions(CommandSender sender,
                                       String commandString,
                                       Consumer<PluginCommand> consumer) {
        List<String> parts = new ArrayList<>(Arrays.asList(commandString.split(" ")));
        if (parts.get(0).isEmpty()) {
            parts.remove(0);
        }
        var command = new PluginCommandTabCompletable(injection, sender, parts);
        consumer.accept(command);
        
        return getCompletions(command);
    }
    
    private List<String> getCompletions(PluginCommandTabCompletable command) {
        if (command.hasChild()) {
            return getCompletions(command.getChild());
        } else if (command.getState() == PluginCommand.State.MISSING_ARG ||
                   command.getState() == PluginCommand.State.CLEAN) {
            return command.getTabCompletionOptions();
        } else {
            return Collections.emptyList();
        }
    }
}
