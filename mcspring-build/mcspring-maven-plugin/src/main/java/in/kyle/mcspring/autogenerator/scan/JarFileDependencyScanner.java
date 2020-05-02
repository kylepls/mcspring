package in.kyle.mcspring.autogenerator.scan;

import in.kyle.mcspring.annotation.PluginDepend;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@RequiredArgsConstructor
public class JarFileDependencyScanner {

    private final URLClassLoader classLoader;
    private final List<JarFile> jarFiles;

    public List<PluginDepend> getPluginDependAnnotations() {
        List<PluginDepend> list = new ArrayList<>();
        jarFiles.forEach(jarFile -> list.addAll(findJarFileDependencies(jarFile)));
        return list;
    }

    @SneakyThrows
    private List<PluginDepend> findJarFileDependencies(JarFile jarFile) {
        List<PluginDepend> dependList = new ArrayList<>();
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while(enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            if(isClassFile(entry)) {
                String className = getClassName(entry);
                addDependencyIfPresent(className, dependList);
            }
        }
        return dependList;
    }

    private void addDependencyIfPresent(String className, List<PluginDepend> destination) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if(clazz.isAnnotationPresent(PluginDepend.class)) {
                destination.add(clazz.getAnnotation(PluginDepend.class));
            }
            //Catching a throwable for optional dependencies not being present.
            //It throws a ClassNotFoundException but for some reason we can't catch
            //then handle it because WONK.
        } catch (Throwable ignored) {

        }
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
