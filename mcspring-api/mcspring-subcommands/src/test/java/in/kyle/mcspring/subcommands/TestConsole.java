package in.kyle.mcspring.subcommands;

import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import in.kyle.mcspring.command.SimpleMethodInjection;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestConsole {
    
    private final SimpleMethodInjection injection;
    
    public void run(CommandSender sender, String commandString, Consumer<PluginCommand> consumer) {
        List<String> parts = new ArrayList<>(Arrays.asList(commandString.split(" ")));
        if (parts.get(0).isEmpty()) {
            parts.remove(0);
        }
        PluginCommand command = new PluginCommand(injection, sender, parts, new ArrayList<>());
        consumer.accept(command);
    }
}
