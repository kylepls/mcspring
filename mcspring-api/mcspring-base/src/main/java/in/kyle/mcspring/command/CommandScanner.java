package in.kyle.mcspring.command;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import in.kyle.mcspring.util.SpringScanner;
import lombok.AllArgsConstructor;
import lombok.var;

@Component
@AllArgsConstructor
class CommandScanner implements ApplicationContextAware {
    
    private final CommandController controller;
    private final SpringScanner scanner;
    private final CommandFactory commandFactory;
    
    private final Set<Method> registeredCommands = new HashSet<>();
    
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<Method, Object> scan = scanner.scanMethods(Command.class);
        for (var e : scan.entrySet()) {
            if (!registeredCommands.contains(e.getKey())) {
                Command command = e.getKey().getAnnotation(Command.class);
                String name = command.value();
                var bukkitCommand = commandFactory.makeCommand(e.getKey(), e.getValue(), name);
                controller.registerCommand(bukkitCommand);
                registeredCommands.add(e.getKey());
            }
        }
    }
    
    interface CommandFactory {
        org.bukkit.command.Command makeCommand(Method method, Object object, String name);
    }
}
