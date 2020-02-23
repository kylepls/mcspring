package in.kyle.mcspring.testplugin;

import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;

@Component
class TestCommand {
    
    @Command("test")
    public String playerSender(Player sender, String command) {
        return command + " works!";
    }
}
