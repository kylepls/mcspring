package in.kyle.mcspring.autogenerator.scan;

import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.autogenerator.util.FileUtility;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProjectDependencyScanner implements PluginDependAnnotationScanner {

    private final URLClassLoader classLoader;
    private final File sourcesFolder;

    @SneakyThrows
    @Override
    public List<PluginDepend> getScannedAnnotations() {
        return FileUtility.getFilesInDirectory(sourcesFolder)
                .stream()
                .filter(this::isClass)
                .map(this::getClassName)
                .map(this::loadClass)
                .filter(clazz -> clazz.isAnnotationPresent(PluginDepend.class))
                .map(clazz -> clazz.getAnnotation(PluginDepend.class))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Class<?> loadClass(String className) {
        return classLoader.loadClass(className);
    }

    private String getClassName(File classFile) {
        String fullPath = classFile.getPath();
        String classNameWithPackage = fullPath.replace(sourcesFolder.getPath(), "").replace("\\", ".");
        return classNameWithPackage.substring(1, classNameWithPackage.length() - 6);
    }

    private boolean isClass(File file) {
        return file.getName().endsWith(".class");
    }
}
