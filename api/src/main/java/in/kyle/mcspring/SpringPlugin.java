package in.kyle.mcspring;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public abstract class SpringPlugin extends JavaPlugin {
    
    private ConfigurableApplicationContext context;
    
    @Override
    public final void onEnable() {
        initSpring();
    }
    
    @Override
    public final void onDisable() {
        if (context != null) {
            context.close();
            context = null;
        }
    }
    
    private void initSpring() {
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        
        StaticSpring.setupLogger();
        
        String mainPackage = getMainPackage();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        if (StaticSpring.hasParent()) {
            builder.parent(StaticSpring.getParentContainer());
        }
        
        context = builder.sources(Config.class, SpringSpigotSupport.class)
                .resourceLoader(loader)
                .bannerMode(Banner.Mode.OFF)
                .properties("spigot.plugin=" + getName())
                .properties("main=" + mainPackage)
                .logStartupInfo(false)
                .run();
        StaticSpring.setParent(context);
    }
    
    private String getMainPackage() {
        String mainPackage = getDescription().getMain();
        return mainPackage.substring(0, mainPackage.lastIndexOf("."));
    }
    
    @SuppressWarnings("SpringComponentScan")
    @Configuration
    @ComponentScan(basePackages = "${main}")
    static class Config {
    }
}
