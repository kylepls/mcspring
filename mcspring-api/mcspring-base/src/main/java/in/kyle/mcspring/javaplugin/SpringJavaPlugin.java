package in.kyle.mcspring.javaplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;

import in.kyle.mcspring.SpringLoader;

// This has to be written in Java for loading
public class SpringJavaPlugin extends JavaPlugin {
    
    private SpringLoader impl;
    
    @Override
    public void onEnable() {
        try {
            new McSpringLoader().launch(getClassLoader());
            impl = new SpringLoader(this, getClassLoader());
            impl.onEnable();
        } catch (Exception e) {
            getLogger().info("MCSpring failed to load");
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void onDisable() {
        impl.onDisable();
    }
    
    static class McSpringLoader extends JarLauncher {
        public void launch(ClassLoader classLoader) throws Exception {
            Iterator<Archive> activeArchives = getClassPathArchivesIterator();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            
            while (activeArchives.hasNext()) {
                addURL.invoke(classLoader, activeArchives.next().getUrl());
            }
        }
        
        @Override
        protected String getMainClass() {
            return "";
        }
    }
}
