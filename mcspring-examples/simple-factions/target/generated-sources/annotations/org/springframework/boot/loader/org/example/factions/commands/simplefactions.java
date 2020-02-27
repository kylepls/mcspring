package org.springframework.boot.loader.org.example.factions.commands;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import in.kyle.mcspring.SpringPlugin;

public class simplefactions extends JavaPlugin {

    public void onEnable() {
        try {
            new PluginJarLauncher().launch(new String[0]);
            SpringPlugin.setup(this, MainPluginConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to launch mcspring", e);
        }
    }
    
    public void onDisable() {
        SpringPlugin.teardown(this);
    }

    @SpringBootApplication(scanBasePackages = {"org.example.factions.controller",
    "org.example.factions.commands"})
    static class MainPluginConfig {
    }
    
    static class PluginJarLauncher extends JarLauncher {
        @Override
        protected void launch(String[] args) throws Exception {
            List<Archive> springDepArchives = getClassPathArchives();
            URLClassLoader classLoader = (URLClassLoader) this.getClass().getClassLoader();

            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            for (Archive archive : springDepArchives) {
                addURL.invoke(classLoader, archive.getUrl());
            }
        }
        
        @Override
        protected String getMainClass() {
            return "";
        }
    }
}
