import in.kyle.mcspring.SpringPlugin;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.loader.ExecutableArchiveLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.jar.JarFile;

import org.bukkit.plugin.java.JavaPlugin;

public class {name} extends JavaPlugin {

    public static PluginMain INSTANCE;

    public void onEnable() {
        INSTANCE = this;
        try {
            new PluginJarLauncher().launch(new String[0]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to launch mcspring", e);
        }
    }

    Class<?> getConfiguration() {
        return MainPluginConfig.class;
    }

    @Configuration
    {scans}
    static class MainPluginConfig {
    }
    
    static class PluginJarLauncher extends JarLauncher {
        @Override
        protected void launch(String[] args) throws Exception {
            super.launch(args);
        }
    }
}
