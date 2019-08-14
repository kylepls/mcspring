package com.joshb.mcspringboot.command;

import com.joshb.mcspringboot.util.SpringScanner;

import org.bukkit.command.CommandSender;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

import lombok.AllArgsConstructor;

@Component
@Configuration
@AllArgsConstructor
class CommandScanner implements ApplicationContextAware {
    
    private final CommandController controller;
    private final SpringScanner scanner;
    private final SimpleMethodInjection methodInjection;
    
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<Method, Object> scan = scanner.scanMethods(Command.class);
        for (Map.Entry<Method, Object> e : scan.entrySet()) {
            Command command = e.getKey().getAnnotation(Command.class);
            String name = command.value();
            controller.registerCommand(new org.bukkit.command.Command(name) {
                @Override
                public boolean execute(CommandSender commandSender, String label, String[] args) {
                    try {
                        Object result = methodInjection.invoke(e.getKey(),
                                                               e.getValue(),
                                                               commandSender,
                                                               args,
                                                               label);
                        if (result != null) {
                            commandSender.sendMessage(result.toString());
                        }
                    } catch (RuntimeException exception) {
                        throw new RuntimeException(
                                "Could not invoke method " + e.getKey().getName(), exception);
                    }
                    return false;
                }
            });
        }
    }
}
