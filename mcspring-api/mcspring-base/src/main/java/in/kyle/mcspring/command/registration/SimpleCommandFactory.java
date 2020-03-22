package in.kyle.mcspring.command.registration;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import in.kyle.mcspring.RequiresSpigot;
import in.kyle.mcspring.command.SimpleMethodInjection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;

@Component
@RequiredArgsConstructor
@RequiresSpigot
class SimpleCommandFactory implements BukkitCommandRegistration.CommandFactory {
    
    private final SimpleMethodInjection injection;
    private final Set<CommandResolver> commandResolvers;
    private final Plugin plugin;
    
    @SneakyThrows
    public org.bukkit.command.Command makeCommand(Method method, Object object, String name) {
        var constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        constructor.setAccessible(true);
        PluginCommand command = constructor.newInstance(name, plugin);
        CommandExecutor executor = makeExecutor(method, object);
        command.setExecutor(executor);
        return command;
    }
    
    CommandExecutor makeExecutor(Method method, Object object) {
        return (sender, bukkitCommand, label, args) -> {
            try {
                var command = new CommandResolver.Command(sender, args, label);
                List<Resolver> resolvers = commandResolvers.stream()
                        .map(r -> r.makeResolver(command))
                        .collect(Collectors.toList());
                var result = injection.invoke(method, object, resolvers, sender, args, label);
                if (result != null) {
                    sender.sendMessage(result.toString());
                }
            } catch (RuntimeException exception) {
                throw new RuntimeException("Could not invoke method " + method.getName(),
                                           exception);
            }
            return true;
        };
    }
}
