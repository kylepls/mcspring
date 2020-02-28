package in.kyle.mcspring.command;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Consumer;

import in.kyle.mcspring.subcommands.tab.TabDiscovery;
import lombok.SneakyThrows;
import lombok.var;

@Primary
@Component
class TabCommandFactory extends SimpleCommandFactory {
    
    private final TabDiscovery tabDiscovery;
    
    public TabCommandFactory(SimpleMethodInjection injection,
                             Set<CommandResolver> commandResolvers,
                             Plugin plugin,
                             TabDiscovery tabDiscovery) {
        super(injection, commandResolvers, plugin);
        this.tabDiscovery = tabDiscovery;
    }
    
    @Override
    public Command makeCommand(Method method, Object object, String name) {
        var command = (PluginCommand) super.makeCommand(method, object, name);
        if (method.getParameterCount() == 1 && method.getParameters()[0].getType()
                .isAssignableFrom(in.kyle.mcspring.subcommands.PluginCommand.class)) {
            command.setTabCompleter(makeTabCompleter(method, object));
        }
        return command;
    }
    
    private Consumer<in.kyle.mcspring.subcommands.PluginCommand> methodToConsumer(Method method,
                                                                                  Object object) {
        return pluginCommand -> {
            method.setAccessible(true);
            invoke(method, object, pluginCommand);
        };
    }
    
    @SneakyThrows
    private Object invoke(Method method,
                          Object object,
                          in.kyle.mcspring.subcommands.PluginCommand command) {
        return method.invoke(object, command);
    }
    
    private TabCompleter makeTabCompleter(Method method, Object object) {
        return (sender, command, s, strings) -> {
            var consumer = methodToConsumer(method, object);
            return tabDiscovery.getCompletions(sender, String.join(" ", strings), consumer);
        };
    }
}
