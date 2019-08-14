import org.springframework.boot.autoconfigure.SpringBootApplication;

// @formatter::off
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.loader.mcspring.McSpringLoader;

import in.kyle.mcspring.SpringPlugin;
// @formatter::on

public class {name} extends JavaPlugin {

    private McSpringLoader loader;

    public void onEnable() {
        try {
            new McSpringLoader().launch(getClassLoader());
            SpringPlugin.setup(this, MainPluginConfig.class);
        } catch (Exception ignored){
            getLogger().warning("MCSpring Failed to load " + getName());
            // error will be logged by Spring
        }
    }

    public void onDisable() {
        SpringPlugin.teardown(this);
//        if (loader != null) loader.close();
        loader = null;
    }

    @SpringBootApplication(scanBasePackages = {{scans}})
    static class MainPluginConfig {
    }
}
