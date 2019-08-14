package in.kyle.mcspring.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.var;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(Plugin.class)
class SimpleCommandFactory implements BukkitCommandRegistration.CommandFactory {
    
    private final SimpleMethodInjection methodInjection;
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
        return (commandSender, bukkitCommand, label, args) -> {
            try {
                CommandResolver.Command command =
                        new CommandResolver.Command(commandSender, args, label);
                List<Resolver> contextResolvers = commandResolvers.stream()
                        .map(r -> r.makeResolver(command))
                        .collect(Collectors.toList());
                Object result = methodInjection.invoke(method,
                                                       object,
                                                       contextResolvers,
                                                       commandSender,
                                                       args,
                                                       label);
                if (result != null) {
                    commandSender.sendMessage(result.toString());
                }
            } catch (RuntimeException exception) {
                throw new RuntimeException("Could not invoke method " + method.getName(),
                                           exception);
            }
            return true;
        };
    }
}
