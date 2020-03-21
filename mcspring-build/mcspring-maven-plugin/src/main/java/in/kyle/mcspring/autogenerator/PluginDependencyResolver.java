package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.annotation.PluginDepend;
import lombok.SneakyThrows;
import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginDependencyResolver {

    private final List<File> jars;

    public PluginDependencyResolver(Collection<Artifact> dependencies) {
        this.jars = dependencies.stream().map(Artifact::getFile).collect(Collectors.toList());
    }

    public List<URL> getDependencyURLs() {
        return jars.stream().map(this::createJarFileURL).collect(Collectors.toList());
    }

    public List<PluginDepend> resolveAllDependencies() {
        List<PluginDepend> dependencies = new ArrayList<>();
        URLClassLoader classLoader = createDependencyClassLoader();
        jars.forEach(file -> dependencies.addAll(findDependencies(file, classLoader)));
        return dependencies;
    }

    @SneakyThrows
    private List<PluginDepend> findDependencies(File file, URLClassLoader classLoader) {
        JarFile jarFile = new JarFile(file);
        return findDependencies(jarFile, classLoader);
    }

    @SneakyThrows
    private List<PluginDepend> findDependencies(JarFile jarFile, URLClassLoader classLoader) {
        List<PluginDepend> dependList = new ArrayList<>();
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while(enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            if(isClassFile(entry)) {
                String className = getClassName(entry);
                addDependencyIfPresent(className, classLoader, dependList);
            }
        }
        return dependList;
    }

    private void addDependencyIfPresent(String className, URLClassLoader loader, List<PluginDepend> destination) {
        try {
            Class<?> clazz = loader.loadClass(className);
            if(clazz.isAnnotationPresent(PluginDepend.class)) {
                destination.add(clazz.getAnnotation(PluginDepend.class));
            }
            //Catching a throwable for optional dependencies not being present.
            //It throws a ClassNotFoundException but for some reason we can't catch
            //then handle it because WONK.
        } catch (Throwable ignored) {

        }
    }

    @SneakyThrows
    private URLClassLoader createDependencyClassLoader() {
        ClassLoader parent = getClass().getClassLoader();
        URL[] urls = getDependencyURLs().toArray(URL[]::new);
        return new URLClassLoader(urls, parent);
    }

    @SneakyThrows
    private URL createJarFileURL(File jarFile) {
        return new URL("jar:file:" + jarFile.getPath() + "!/");
    }

    private boolean isClassFile(JarEntry entry) {
        return entry.getName().endsWith(".class");
    }

    private String getClassName(JarEntry entry) {
        String fullName = entry.getName();
        String excludeClassExtension = fullName.substring(0, fullName.length() - 6);
        return excludeClassExtension.replace("/", ".");
    }
}
