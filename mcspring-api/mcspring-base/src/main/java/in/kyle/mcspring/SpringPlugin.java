package in.kyle.mcspring;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.plugin.Plugin;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringPlugin {
    
    private static final LinkedHashMap<Plugin, SpringPlugin> SETUP_PLUGINS = new LinkedHashMap<>();
    
    private final Plugin plugin;
    @Getter
    private ConfigurableApplicationContext context;
    
    public static void setup(Plugin plugin, Class<?> config) {
        setupLogger();
        SpringPlugin springPlugin = new SpringPlugin(plugin);
        springPlugin.initSpring(config);
        SETUP_PLUGINS.put(plugin, springPlugin);
    }
    
    public static void teardown(Plugin plugin) {
        SpringPlugin springPlugin = SETUP_PLUGINS.remove(plugin);
        if (springPlugin != null) {
            springPlugin.onDisable(plugin);
        }
    }
    
    public final void onDisable(Plugin plugin) {
        if (context != null) {
            context.close();
            context = null;
            SETUP_PLUGINS.remove(plugin);
        }
    }
    
    private void initSpring(Class<?> config) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        ResourceLoader loader = new DefaultResourceLoader(classLoader);
        Class<?>[] sources = new Class[]{config, SpringSpigotSupport.class};
        if (!SETUP_PLUGINS.isEmpty()) {
            SpringPlugin parent = findParentCandidate();
            builder.parent(parent.getContext());
            sources = Arrays.copyOfRange(sources, 0, 1);
        }
        context = builder.sources(sources)
                .resourceLoader(loader)
                .bannerMode(Banner.Mode.OFF)
                .properties("spigot.plugin=" + plugin.getName())
                .logStartupInfo(true)
                .run();
    }
    
    private static void setupLogger() {
        if (SETUP_PLUGINS.isEmpty()) {
            Logger rootLogger = (Logger) LogManager.getRootLogger();
            rootLogger.setLevel(Level.ALL);
        }
    }
    
    private SpringPlugin findParentCandidate() {
        return SETUP_PLUGINS.entrySet()
                .stream()
                .reduce((a, b) -> b)
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}
