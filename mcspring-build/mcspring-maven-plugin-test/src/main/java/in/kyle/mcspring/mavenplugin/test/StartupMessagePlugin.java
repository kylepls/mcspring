package in.kyle.mcspring.mavenplugin.test;

import in.kyle.mcspring.annotation.PluginDepend;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PluginDepend(plugins = "Vault", soft = true)
public class StartupMessagePlugin {

    @PostConstruct
    void message() {
        Player player = Bukkit.getPlayer("");
    }
}
