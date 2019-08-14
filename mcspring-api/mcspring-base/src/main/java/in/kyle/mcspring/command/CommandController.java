package in.kyle.mcspring.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Controller
@RequiredArgsConstructor
@ConditionalOnBean(Plugin.class)
class CommandController {
    
    private final CommandMap commandMap = getCommandMap();
    private final Plugin plugin;
    
    public void registerCommand(Command command) {
        commandMap.register(command.getLabel(), plugin.getName(), command);
    }
    
    @SneakyThrows
    private CommandMap getCommandMap() {
        Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);
        return (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
    }
    
    @Bean
    CommandMap commandMap() {
        return commandMap;
    }
}
