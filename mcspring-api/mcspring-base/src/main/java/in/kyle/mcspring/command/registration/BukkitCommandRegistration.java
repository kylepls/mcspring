package in.kyle.mcspring.command.registration;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

import in.kyle.mcspring.RequiresSpigot;
import in.kyle.mcspring.command.Command;
import lombok.RequiredArgsConstructor;
import lombok.var;

@Component
@RequiredArgsConstructor
@RequiresSpigot
class BukkitCommandRegistration implements CommandRegistration {
    
    private final CommandController controller;
    private final CommandFactory commandFactory;
    
    @Override
    public void register(Command command, Method method, Object object) {
        String name = command.value();
        var bukkitCommand = commandFactory.makeCommand(method, object, name);
        bukkitCommand.setAliases(Arrays.asList(command.aliases()));
        bukkitCommand.setDescription(command.description());
        bukkitCommand.setUsage(command.usage());
        bukkitCommand.setPermission(command.permission());
        bukkitCommand.setPermissionMessage(command.permissionMessage());
        controller.registerCommand(bukkitCommand);
    }
    
    interface CommandFactory {
        org.bukkit.command.Command makeCommand(Method method, Object object, String name);
    }
}
