package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.autogenerator.scan.JarFileDependencyScanner;
import in.kyle.mcspring.autogenerator.scan.ProjectDependencyScanner;
import lombok.SneakyThrows;
import org.apache.maven.artifact.Artifact;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ProjectDependencyResolver {

    private final URLClassLoader fullyQualifiedClassLoader;
    private final File sourcesFolder;
    private final List<File> jars;

    public ProjectDependencyResolver(URLClassLoader fullyQualifiedClassLoader, File sourcesFolder, Collection<Artifact> dependencies) {
        this.fullyQualifiedClassLoader = fullyQualifiedClassLoader;
        this.sourcesFolder = sourcesFolder;
        this.jars = dependencies.stream().map(Artifact::getFile).collect(Collectors.toList());
    }

    @SneakyThrows
    private List<JarFile> getJarFiles() {
        return jars.stream().map(this::getAsJarFile).collect(Collectors.toList());
    }

    @SneakyThrows
    private JarFile getAsJarFile(File file) {
        return new JarFile(file);
    }

    public List<PluginDepend> resolveAllDependencies() {
        List<PluginDepend> dependencies = new ArrayList<>();
        List<JarFile> jars = getJarFiles();
        JarFileDependencyScanner jarFileDependencyScanner = new JarFileDependencyScanner(fullyQualifiedClassLoader, jars);
        ProjectDependencyScanner projectDependencyScanner = new ProjectDependencyScanner(fullyQualifiedClassLoader, sourcesFolder);
        dependencies.addAll(jarFileDependencyScanner.getScannedAnnotations());
        dependencies.addAll(projectDependencyScanner.getScannedAnnotations());
        return dependencies;
    }

}
