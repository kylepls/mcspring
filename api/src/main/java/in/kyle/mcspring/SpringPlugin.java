package in.kyle.mcspring;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.remove.PluginJarLauncher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringPlugin {
    
    private static final Map<Plugin, SpringPlugin> PLUGINS = new HashMap<>();
    
    private final JavaPlugin plugin;
    private ConfigurableApplicationContext context;
    
    public static void setup(JavaPlugin plugin) {
        SpringPlugin springPlugin = new SpringPlugin(plugin);
        PLUGINS.put(plugin, springPlugin);
        springPlugin.onEnable();
    }
    
    public static void teardown(JavaPlugin plugin) {
        SpringPlugin springPlugin = PLUGINS.remove(plugin);
        springPlugin.onDisable();
    }
    
    public void onEnable() {
        new PluginJarLauncher().launch(plugin.getClass().getClassLoader());
        initSpring();
    }
    
    public final void onDisable() {
        if (context != null) {
            context.close();
            context = null;
        }
    }
    
    Class<?> getConfiguration() {
        return Config.class;
    }
    
    private void initSpring() {
        ResourceLoader loader = new DefaultResourceLoader(getClass().getClassLoader());
        
        StaticSpring.setupLogger();
        
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        if (StaticSpring.hasParent()) {
            builder.parent(StaticSpring.getParentContainer());
        }
        
        context = builder.sources(getConfiguration(), SpringSpigotSupport.class)
                .resourceLoader(loader)
                .bannerMode(Banner.Mode.OFF)
                .properties("spigot.plugin=" + plugin.getName())
                .properties("main=" + getMainPackage())
                .logStartupInfo(false)
                .run();
        StaticSpring.setParent(context);
    }
    
    private String getMainPackage() {
        String mainPackage = plugin.getDescription().getMain();
        return mainPackage.substring(0, mainPackage.lastIndexOf("."));
    }
    
    @SuppressWarnings("SpringComponentScan")
    @Configuration
    @ComponentScan(basePackages = "${main}")
    static class Config {
    }
}
