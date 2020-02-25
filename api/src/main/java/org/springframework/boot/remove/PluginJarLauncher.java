package org.springframework.boot.remove;

import org.springframework.boot.loader.ExecutableArchiveLauncher;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.jar.JarFile;

// todo remove
public class PluginJarLauncher extends JarLauncher {
    public void launch(ClassLoader classLoader) {
        try {
            new PluginJarLauncher().launch(new String[0]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to launch mcspring", e);
        }
        JarFile.registerUrlProtocolHandler();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            Class<?> aClass = Class.forName("in.kyle.mcspring.SpringPlugin");
            //            getLogger().info("Found " + aClass.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected boolean isNestedArchive(Archive.Entry entry) {
        return false;
    }
}
