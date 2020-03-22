package in.kyle.mcspring.command.registration;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.springframework.stereotype.Controller;

import in.kyle.mcspring.RequiresSpigot;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequiresSpigot
class CommandController {
    
    private final CommandMap commandMap;
    private final Plugin plugin;
    
    public void registerCommand(Command command) {
        commandMap.register(command.getLabel(), plugin.getName(), command);
    }
}
