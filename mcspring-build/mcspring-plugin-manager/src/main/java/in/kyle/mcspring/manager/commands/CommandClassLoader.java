package in.kyle.mcspring.manager.commands;

import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.subcommands.PluginCommand;

@Component
class CommandClassLoader {
    
    @Command(value = "classloader",
             description = "Show ClassLoader information for a specific class",
             usage = "/classloader <class>")
    void classLoader(PluginCommand command) {
        command.withString();
        command.then(this::executeClassLoader);
        command.otherwise("Usage: /classloader <class>");
    }
    
    String executeClassLoader(String clazz) {
        try {
            Class<?> aClass = Class.forName(clazz);
            String classLoader = aClass.getClassLoader().toString();
            String protectionDomain =
                    aClass.getProtectionDomain().getCodeSource().getLocation().toString();
            return String.format("ClassLoader: %s\nDomain: %s", classLoader, protectionDomain);
        } catch (ClassNotFoundException e) {
            return String.format("Class %s not found", clazz);
        }
    }
}
