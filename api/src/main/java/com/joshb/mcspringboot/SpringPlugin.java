package com.joshb.mcspringboot;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;

public abstract class SpringPlugin extends JavaPlugin {
    
    private ConfigurableApplicationContext context;
    private ClassLoader defaultClassLoader;
    
    @Override
    public final void onEnable() {
        defaultClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClassLoader());
        load();
        enable();
    }
    
    @Override
    public final void onDisable() {
        if (defaultClassLoader != null) {
            Thread.currentThread().setContextClassLoader(defaultClassLoader);
        }
        if (context != null) {
            context.close();
            context = null;
            disable();
        }
    }
    
    public void enable() {
        
    }
    
    public void disable() {
        
    }
    
    private void load() {
        if (context != null) {
            context.close();
        }
        ResourceLoader loader = new DefaultResourceLoader(getClassLoader());
        
        
        String mainPackage = getDescription().getMain();
        mainPackage = mainPackage.substring(0, mainPackage.lastIndexOf("."));
        context = new SpringApplicationBuilder().sources(Config.class,
                                                         SpringSpigotSupport.class)
                .resourceLoader(loader)
                .bannerMode(Banner.Mode.OFF)
                .properties("spigot.plugin=" + getName())
                .properties("main=" + mainPackage)
                .logStartupInfo(false)
                .run();
    }
    
    @SuppressWarnings("SpringComponentScan")
    @Configuration
    @ComponentScan(basePackages = "${main}")
    @EnableScheduling
    @EnableAspectJAutoProxy
    static class Config {
    }
}
