package in.kyle.mcspring.manager.commands;

import org.bukkit.Server;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;

@Component
class CommandAbout {
    @Command(value = "about",
             description = "Provides information about current library versions being used")
    String about(BuildProperties properties, Server server) {
        return String.format(
                "Plugin Name: %s\nPlugin Version: %s\nSpring Version: %s\nBukkit Version: %s",
                properties.getName(),
                properties.getVersion(),
                SpringVersion.getVersion(),
                server.getBukkitVersion());
    }
}
