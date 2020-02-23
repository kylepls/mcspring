package in.kyle.mcspring.command;

import org.bukkit.command.CommandSender;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import in.kyle.mcspring.util.SpringScanner;
import lombok.AllArgsConstructor;

@Component
@Configuration
@AllArgsConstructor
class CommandScanner implements ApplicationContextAware {
    
    private final CommandController controller;
    private final SpringScanner scanner;
    private final SimpleMethodInjection methodInjection;
    private final Set<CommandResolver> commandResolvers;
    
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<Method, Object> scan = scanner.scanMethods(Command.class);
        for (Map.Entry<Method, Object> e : scan.entrySet()) {
            Command command = e.getKey().getAnnotation(Command.class);
            String name = command.value();
            controller.registerCommand(makeCommand(e, name));
        }
    }
    
    private org.bukkit.command.Command makeCommand(Map.Entry<Method, Object> e, String name) {
        return new org.bukkit.command.Command(name) {
            @Override
            public boolean execute(CommandSender commandSender, String label, String[] args) {
                try {
                    CommandResolver.Command command =
                            new CommandResolver.Command(commandSender, args, label);
                    Set<Resolver> contextResolvers = commandResolvers.stream()
                            .map(r -> r.makeResolver(command))
                            .collect(Collectors.toSet());
                    Object result = methodInjection.invoke(e.getKey(),
                                                           e.getValue(),
                                                           contextResolvers,
                                                           commandSender,
                                                           args,
                                                           label);
                    if (result != null) {
                        commandSender.sendMessage(result.toString());
                    }
                } catch (RuntimeException exception) {
                    throw new RuntimeException("Could not invoke method " + e.getKey().getName(),
                                               exception);
                }
                return false;
            }
        };
    }
}
