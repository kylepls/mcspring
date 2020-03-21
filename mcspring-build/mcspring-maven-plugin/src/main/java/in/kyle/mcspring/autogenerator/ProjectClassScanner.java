package in.kyle.mcspring.autogenerator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;


//Objective: Scan all classes in the provider folder and check for spring annotations.
//If present, get the class package and add it to a list for springs package scan at runtime
@RequiredArgsConstructor
class ProjectClassScanner {

    private static final List<Class<? extends Annotation>> SPRING_ANNOTATIONS = Arrays.asList(
            Service.class,
            Controller.class,
            Component.class
    );

    private final File classesFolder;
    private final List<URL> projectDependencyJars;
    private final Set<String> packages = new HashSet<>();

    public Set<String> getPackagesThatUseSpring() {
        return Collections.unmodifiableSet(packages);
    }

    public void findPackages() {
        List<File> classFiles = getClassFiles();
        URLClassLoader classLoader = createClassLoader();
        classFiles.forEach(file -> addPackageIfFound(classLoader, file));
    }

    private void addPackageIfFound(URLClassLoader classLoader, File classFile) {
        String className = getClassName(classFile);
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if(hasSpringAnnotation(clazz)) {
                packages.add(clazz.getPackage().getName());
            }
        } catch (Throwable ignored){
            //Proper error handling here.
        }
    }


    private String getClassName(File classFile) {
        String fullPath = classFile.getPath();
        String classNameWithPackage = fullPath.replace(classesFolder.getPath(), "").replace("\\", ".");
        return classNameWithPackage.substring(1, classNameWithPackage.length() - 6);
    }

    private List<File> getClassFiles() {
        return getFilesInDirectory(classesFolder)
                .stream()
                .filter(file -> file.getName().endsWith(".class"))
                .collect(Collectors.toList());
    }

    private boolean hasSpringAnnotation(Class<?> clazz) {
        return SPRING_ANNOTATIONS.stream().anyMatch(clazz::isAnnotationPresent);
    }

    @SneakyThrows
    private URLClassLoader createClassLoader() {
        List<URL> classPathList = new ArrayList<>(projectDependencyJars);
        classPathList.add(classesFolder.toURI().toURL());
        return new URLClassLoader(classPathList.toArray(new URL[0]), getClass().getClassLoader());
    }

    private List<File> getFilesInDirectory(File directory) {
        List<File> files = new ArrayList<>();
        File[] filesInDirectory = directory.listFiles();
        if(filesInDirectory != null) {
            for(File file : filesInDirectory) {
                if(file.isDirectory()) {
                    files.addAll(getFilesInDirectory(file));
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
