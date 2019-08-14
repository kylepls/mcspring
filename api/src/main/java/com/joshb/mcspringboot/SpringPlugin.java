package com.joshb.mcspringboot;

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
    private ClassLoader defaultClassLoader;
    
    @Override
    public final void onEnable() {
        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        load();
    }
    
    @Override
    public final void onDisable() {
        if (defaultClassLoader != null) {
            Thread.currentThread().setContextClassLoader(defaultClassLoader);
        }
        if (context != null) {
            context.close();
            context = null;
        }
    }
    
    private void load() {
        if (context != null) {
            context.close();
        }
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        
        String mainPackage = getDescription().getMain();
        mainPackage = mainPackage.substring(0, mainPackage.lastIndexOf("."));
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
    
    @SuppressWarnings("SpringComponentScan")
    @Configuration
    @ComponentScan(basePackages = "${main}")
    static class Config {
    }
}
