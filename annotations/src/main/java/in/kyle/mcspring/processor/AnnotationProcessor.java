package in.kyle.mcspring.processor;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import in.kyle.mcspring.processor.annotation.PluginDepend;
import in.kyle.mcspring.processor.annotation.SpringPlugin;
import in.kyle.mcspring.processor.util.MainClassCreator;

// Adapted from https://hub.spigotmc.org/stash/projects/SPIGOT/repos/plugin-annotations/browse
// /src/main/java/org/bukkit/plugin/java/annotation/PluginAnnotationProcessor.java
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {
    
    private Writer yml;
    private String mainClass = "PluginMain";
    private boolean created = false;
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (created) {
            return true;
        }
        try {
            this.processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, "Generating Plugin Data...");
            process(env);
            created = true;
        } catch (Exception e) {
            this.processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getStackTrace(e));
            return false;
        }
        return true;
    }
    
    private Set<String> findPackage(RoundEnvironment env) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(Component.class);
        elements.addAll((Set) env.getElementsAnnotatedWith(Controller.class));
        elements.addAll((Set) env.getElementsAnnotatedWith(Service.class));
        Set<String> packages = new HashSet<>();
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                TypeElement te = (TypeElement) element;
                String packageName = getPackageFromFqn(te.getQualifiedName().toString());
                packages.add(packageName);
            }
        }
        return packages;
    }
    
    private String getRootPackage(Set<String> packages) {
        return packages.stream().min(Comparator.comparingInt(String::length)).orElse("ignore");
    }
    
    private String getPackageFromFqn(String fqn) {
        if (fqn.contains(".")) {
            return fqn.substring(0, fqn.lastIndexOf("."));
        } else {
            return fqn;
        }
    }
    
    private void process(RoundEnvironment env) throws Exception {
        FileObject ymlFile = processingEnv.getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
        yml = ymlFile.openWriter();
        Set<String> packages = findPackage(env);
        String rootPackage = getRootPackage(packages);
        mainClass = rootPackage + "." + mainClass;
        FileObject main = processingEnv.getFiler().createSourceFile(mainClass);
        MainClassCreator.generateMain(main, mainClass, rootPackage, packages);
        addRequired(env);
        addDependencies(env);
        yml.flush();
        yml.close();
    }
    
    private void addRequired(RoundEnvironment env) throws IOException {
        yml.write(String.format("main: %s\n", mainClass));
        String name = processOne(env,
                                 SpringPlugin.class,
                                 SpringPlugin::name,
                                 "spring-plugin-default-name");
        yml.write(String.format("name: %s\n", name));
        String version = processOne(env, SpringPlugin.class, SpringPlugin::version, "0.0.1");
        yml.write(String.format("version: %s\n", version));
        String description = processOne(env, SpringPlugin.class, SpringPlugin::description, "");
        yml.write(String.format("description: %s\n", description));
    }
    
    private void addDependencies(RoundEnvironment env) throws IOException {
        Set<String> hardDepend = new HashSet<>();
        Set<String> softDepend = new HashSet<>();
        processAll(env, PluginDepend.class, depend -> {
            if (depend.soft()) {
                return softDepend.addAll(Arrays.asList(depend.plugins()));
            } else {
                return hardDepend.addAll(Arrays.asList(depend.plugins()));
            }
        });
        if (!hardDepend.isEmpty()) {
            String dependString = String.join(", ", hardDepend);
            yml.write(String.format("depend: [%s]\n", dependString));
        }
        if (!softDepend.isEmpty()) {
            String dependString = String.join(", ", softDepend);
            yml.write(String.format("softdepend: [%s]\n", dependString));
        }
    }
    
    private static <T extends Annotation, R> R processOne(RoundEnvironment env,
                                                          Class<T> annotation,
                                                          Function<T, R> function,
                                                          R defaultValue) {
        List<R> rs = processAll(env, annotation, function);
        if (!rs.isEmpty()) {
            return rs.get(0);
        } else {
            return defaultValue;
        }
    }
    
    private static <T extends Annotation, R> List<R> processAll(RoundEnvironment env,
                                                                Class<T> annotation,
                                                                Function<T, R> consumer) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(annotation);
        return elements.stream()
                .flatMap(e -> Arrays.stream(e.getAnnotationsByType(annotation)))
                .map(consumer)
                .collect(Collectors.toList());
    }
}
