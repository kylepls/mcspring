// Package name for hiding the class in the final jar
package org.springframework.boot.loader.in.kyle.mcspring.javaplugin;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.lang.NonNull;

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
            //            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            //            addURL.setAccessible(true);
            UnsafeClassLoaderAccess unsafe = new UnsafeClassLoaderAccess((URLClassLoader) classLoader);
            while (activeArchives.hasNext()) {
                //                addURL.invoke(classLoader, activeArchives.next().getUrl());
                unsafe.addURL(activeArchives.next().getUrl());
            }
        }

        @Override
        protected String getMainClass() {
            return "";
        }
    }

    // https://github.com/slimjar/slimjar
    private static class UnsafeClassLoaderAccess {
        private static final sun.misc.Unsafe UNSAFE;

        static {
            try {
                Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                UNSAFE = (sun.misc.Unsafe) unsafeField.get(null);
            } catch (Throwable t) {
                throw new Error("Unable to find unsafe", t);
            }
        }

        private final Collection<URL> unopenedURLs;
        private final Collection<URL> pathURLs;

        @SuppressWarnings("unchecked")
        UnsafeClassLoaderAccess(URLClassLoader classLoader) {
            Collection<URL> unopenedURLs;
            Collection<URL> pathURLs;
            try {
                Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (Throwable e) {
                unopenedURLs = null;
                pathURLs = null;
            }

            this.unopenedURLs = unopenedURLs;
            this.pathURLs = pathURLs;
        }

        private static Object fetchField(final Class<?> clazz, final Object object, final String name)
                throws NoSuchFieldException {
            Field field = clazz.getDeclaredField(name);
            long offset = UNSAFE.objectFieldOffset(field);
            return UNSAFE.getObject(object, offset);
        }

        public void addURL(@NonNull URL url) {
            if (this.unopenedURLs == null || this.pathURLs == null) {
                throw new NullPointerException("unopenedURLs or pathURLs");
            }

            this.unopenedURLs.add(url);
            this.pathURLs.add(url);
        }
    }
}
