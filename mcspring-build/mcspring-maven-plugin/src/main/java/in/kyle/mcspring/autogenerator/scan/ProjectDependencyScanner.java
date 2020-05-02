package in.kyle.mcspring.autogenerator.scan;

import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.autogenerator.FileUtility;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProjectDependencyScanner {

    private final URLClassLoader classLoader;
    private final File sourcesFolder;

    @SneakyThrows
    public List<PluginDepend> getPluginDependAnnotations() {
        List<PluginDepend> list = new ArrayList<>();
        List<File> files = FileUtility.getFilesInDirectory(sourcesFolder);
        for(File file : files) {
            if(isClass(file)) {
                String className = getClassName(file);
                Class<?> clazz = classLoader.loadClass(className);
                if(clazz.isAnnotationPresent(PluginDepend.class)) {
                    list.add(clazz.getAnnotation(PluginDepend.class));
                }
            }
        }
        return list;
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
