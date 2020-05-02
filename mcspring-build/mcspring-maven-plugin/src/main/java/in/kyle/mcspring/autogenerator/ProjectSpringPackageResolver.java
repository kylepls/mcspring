package in.kyle.mcspring.autogenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;


//Objective: Scan all classes in the provider folder and check for spring annotations.
//If present, get the class package and add it to a list for springs package scan at runtime
@RequiredArgsConstructor
class ProjectSpringPackageResolver {

    private static final List<Class<? extends Annotation>> SPRING_ANNOTATIONS = Arrays.asList(
            Service.class,
            Controller.class,
            Component.class
    );

    private final URLClassLoader fullyQualifiedClassLoader;
    private final File classesFolder;
    private final Set<String> packages = new HashSet<>();

    public Set<String> getPackagesThatUseSpring() {
        return Collections.unmodifiableSet(packages);
    }

    public void findPackagesThatUseSpring() {
        List<File> classFiles = getClassFiles();
        classFiles.forEach(this::addPackageIfFound);
    }

    private void addPackageIfFound(File classFile) {
        String className = getClassName(classFile);
        try {
            Class<?> clazz = fullyQualifiedClassLoader.loadClass(className);
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
        return FileUtility.getFilesInDirectory(classesFolder)
                .stream()
                .filter(file -> file.getName().endsWith(".class"))
                .collect(Collectors.toList());
    }

    private boolean hasSpringAnnotation(Class<?> clazz) {
        return SPRING_ANNOTATIONS.stream().anyMatch(clazz::isAnnotationPresent);
    }
}
