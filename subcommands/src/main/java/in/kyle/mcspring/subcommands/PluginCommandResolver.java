package in.kyle.mcspring.subcommands;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import in.kyle.mcspring.command.CommandResolver;
import in.kyle.mcspring.command.Resolver;
import in.kyle.mcspring.command.SimpleMethodInjection;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PluginCommandResolver implements CommandResolver {
    
    private final SimpleMethodInjection injection;
    
    @Override
    public Resolver makeResolver(Command command) {
        return parameter -> parameter.getType().isAssignableFrom(PluginCommand.class) ? Optional.of(
                makeCommand(command)) : Optional.empty();
    }
    
    private PluginCommand makeCommand(Command command) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(command.getArgs()));
        return new PluginCommand(injection, command.getSender(), args, new ArrayList<>());
    }
}
