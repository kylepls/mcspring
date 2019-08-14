package in.kyle.mcspring.test.command;

import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.command.CommandRegistration;
import in.kyle.mcspring.command.CommandResolver;
import in.kyle.mcspring.command.SimpleMethodInjection;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Component
@RequiredArgsConstructor
class TestCommandRegistration implements CommandRegistration {
    
    private final SimpleMethodInjection injection;
    private final Set<CommandResolver> commandResolvers;
    
    private final Map<String, Executor> commandExecutors = new HashMap<>();
    
    @Override
    public void register(Command command, Method method, Object object) {
        var executor = makeExecutor(method, object);
        getAllNames(command).forEach(key -> commandExecutors.put(key, executor));
    }
    
    private List<String> getAllNames(Command command) {
        List<String> commands = new ArrayList<>(Arrays.asList(command.aliases()));
        commands.add(command.value());
        return commands;
    }
    
    private Executor makeExecutor(Method method, Object object) {
        return (sender, label, args) -> {
            var temp = new CommandResolver.Command(sender, args, label);
            var contextResolvers = commandResolvers.stream()
                    .map(r -> r.makeResolver(temp))
                    .collect(Collectors.toList());
            Object result = injection.invoke(method, object, contextResolvers, sender, args, label);
            if (result != null) {
                sender.sendMessage(result.toString());
            }
        };
    }
    
    public void run(String command, CommandSender sender, String label, String[] args) {
        if (commandExecutors.containsKey(command)) {
            commandExecutors.get(command).execute(sender, label, args);
        } else {
            throw new RuntimeException(
                    "Command " + command + " is not registered. Make sure to @Import it");
        }
    }
    
    interface Executor {
        void execute(CommandSender sender, String label, String[] args);
    }
}
