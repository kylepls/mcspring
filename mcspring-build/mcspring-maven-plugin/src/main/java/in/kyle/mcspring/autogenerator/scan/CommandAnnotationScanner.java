package in.kyle.mcspring.autogenerator.scan;

import in.kyle.mcspring.autogenerator.util.FileUtility;
import in.kyle.mcspring.command.Command;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CommandAnnotationScanner {

    private final URLClassLoader classLoader;
    private final File classesFolder;

    public List<Command> getCommandAnnotations() {
        return getClassFiles().stream()
                .map(this::getClassName)
                .map(this::loadClass)
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .filter(method -> method.isAnnotationPresent(Command.class))
                .map(method -> method.getAnnotation(Command.class))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Class<?> loadClass(String name) {
        return classLoader.loadClass(name);
    }

    private String getClassName(File classFile) {
        String fullPath = classFile.getPath();
        String classNameWithPackage = fullPath.replace(classesFolder.getPath(), "").replace("\\", ".");
        return classNameWithPackage.substring(1, classNameWithPackage.length() - 6);
    }

    private List<File> getClassFiles() {
        return FileUtility.getFilesInDirectory(classesFolder)
                .stream()
                .filter(file -> file.getName().endsWith(".class"))
                .collect(Collectors.toList());
    }
}
