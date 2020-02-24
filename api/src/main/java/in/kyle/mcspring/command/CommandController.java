package in.kyle.mcspring.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;

import lombok.SneakyThrows;

@Controller
@Profile("!test")
public class CommandController {
    
    private final CommandMap commandMap = getCommandMap();
    
    public void registerCommand(Command command) {
        commandMap.register(command.getLabel(), command);
    }
    
    @SneakyThrows
    private CommandMap getCommandMap() {
        Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
    }
}
