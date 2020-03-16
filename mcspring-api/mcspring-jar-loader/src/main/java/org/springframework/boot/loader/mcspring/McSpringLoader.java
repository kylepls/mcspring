package org.springframework.boot.loader.mcspring;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import lombok.SneakyThrows;

// Packaging is so that it all blends in with the Spring loader
public class McSpringLoader extends JarLauncher {
    
    @SneakyThrows
    public void launch(ClassLoader classLoader) {
        List<Archive> activeArchives = getClassPathArchives();
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        for (Archive archive : activeArchives) {
            addURL.invoke(classLoader, archive.getUrl());
        }
    }
    
    @Override
    protected String getMainClass() {
        return "";
    }
}
