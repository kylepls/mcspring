package in.kyle.mcspring.processor;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Arrays;
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
import javax.lang.model.type.TypeMirror;
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
    private String mainClass = "main.Plugin";
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
    
    private void process(RoundEnvironment env) throws Exception {
        FileObject ymlFile = processingEnv.getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
        yml = ymlFile.openWriter();
        if (isGenerateMain(env)) {
            FileObject main = this.processingEnv.getFiler().createSourceFile(mainClass);
            this.processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.NOTE, "Main class: " + mainClass);
            MainClassCreator.generateMain(main, mainClass);
        }
        addRequired(env);
        addDependencies(env);
        yml.flush();
        yml.close();
    }
    
    private boolean isGenerateMain(RoundEnvironment env) {
        Set<? extends Element> plugins = env.getElementsAnnotatedWith(SpringPlugin.class);
        if (!plugins.isEmpty()) {
            Element pluginElement = plugins.iterator().next();
            if (pluginElement instanceof TypeElement) {
                TypeElement te = (TypeElement) pluginElement;
                TypeMirror springPluginType = processingEnv.getElementUtils()
                        .getTypeElement(SpringPlugin.class.getName())
                        .asType();
                if (processingEnv.getTypeUtils().isSubtype(te.asType(), springPluginType)) {
                    mainClass = "main." + te.getQualifiedName().toString().replace("-", "");
                }
            }
        }
        return true;
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
