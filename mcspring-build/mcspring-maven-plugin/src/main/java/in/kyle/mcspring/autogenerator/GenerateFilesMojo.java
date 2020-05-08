package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.autogenerator.scan.CommandAnnotationScanner;
import in.kyle.mcspring.autogenerator.scan.ProjectSourceAnnotationScanner;
import in.kyle.mcspring.autogenerator.util.ClassLoadingUtility;
import in.kyle.mcspring.autogenerator.util.MainClassUtilities;
import lombok.val;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.SneakyThrows;


@Mojo(name = "generate-files", defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateFilesMojo extends AbstractMojo {

    private static final List<String> VALID_SCOPES = Arrays.asList("provided", "compile", "runtime");

    @Component
    private MavenProject project;
    private URLClassLoader fullyQualifiedClassLoader;

    @SneakyThrows
    public void execute() {
        initializeClassLoader();
        addGeneratedSourcesDirectory();
        preparePluginYml();
        preparePluginMainClass();
    }

    //Initializes a class loader with all maven dependency classes and project classes.
    public void initializeClassLoader() {
        ClassLoader parent = getClass().getClassLoader();
        File projectSource = getSourceClassesFolder();
        List<File> artifactFiles = getDependencyArtifacts().stream()
                .map(Artifact::getFile)
                .collect(Collectors.toList());
        this.fullyQualifiedClassLoader = ClassLoadingUtility.createClassLoader(parent, artifactFiles, projectSource);
    }

    private File getSourcesOutputDirectory() {
        return new File(getGeneratedSourcesFolder(), "mc-spring/");
    }


    private void addGeneratedSourcesDirectory() {
        File output = getSourcesOutputDirectory();
        if (!output.exists()) {
            output.mkdirs();
        }
        project.addCompileSourceRoot(output.getPath());
    }

    public CommandAnnotationScanner getCommandAnnotationScanner() {
        return new CommandAnnotationScanner(fullyQualifiedClassLoader, getSourceClassesFolder());
    }

    public ProjectDependencyResolver getDependencyResolver() {
        Set<Artifact> artifacts = getDependencyArtifacts();
        File sources = getSourceClassesFolder();
        return new ProjectDependencyResolver(fullyQualifiedClassLoader, sources, artifacts);
    }

    public PluginYamlAttributes getLoadedYamlAttributes() {
        val resolver = getDependencyResolver();
        val cmdScanner = new CommandAnnotationScanner(fullyQualifiedClassLoader, getSourceClassesFolder());
        val attributes = new PluginYamlAttributes(project, resolver.resolveAllDependencies(), cmdScanner.getCommandAnnotations());
        attributes.loadAttributes();
        return attributes;
    }

    public ProjectSourceAnnotationScanner getSourceAnnotationScanner() {
        File sources = getSourceClassesFolder();
        return new ProjectSourceAnnotationScanner(fullyQualifiedClassLoader, sources);
    }

    private void preparePluginYml() {
        getLog().info("Scanning for project dependencies in qualifying scope");
        Set<Artifact> artifacts = getDependencyArtifacts();
        getLog().info(String.format("Dependency scan complete. Found %d project dependencies", artifacts.size()));
        val resolver = getDependencyResolver();
        val cmdScanner = new CommandAnnotationScanner(fullyQualifiedClassLoader, getSourceClassesFolder());
        val attributes = new PluginYamlAttributes(project, resolver.resolveAllDependencies(), cmdScanner.getCommandAnnotations());
        attributes.loadAttributes();
        getLog().info("Finished obtaining data for plugin.yml");
        getLog().info("----------------------------------------------------------------");
        attributes.getAttributes().forEach((key, data) -> getLog().info(key + ": " + data.toString()));
        getLog().info("----------------------------------------------------------------");
        getLog().info("Writing plugin.yml to generated-sources");
        File pluginFile = new File(getSourcesOutputDirectory(), "plugin.yml");
        attributes.writeToFile(pluginFile);
        getLog().info("Write completed");
    }

    private void preparePluginMainClass() {
        getLog().info("Scanning project sources for spring annotations");
        val scanner = new ProjectSourceAnnotationScanner(fullyQualifiedClassLoader, getSourceClassesFolder());
        scanner.findPackagesThatUseSpring();
        Set<String> packages = scanner.getPackagesThatUseSpring();
        getLog().info(String.format("Scan complete. Found %d packages with spring annotation", packages.size()));
        getLog().info("Preparing to generate main class");
        writePluginMain(packages);
        getLog().info("Default main class has been added to generated-sources");
        getLog().info("Auto generation process complete");
    }

    private void writePluginMain(Set<String> packages) {
        String mainClass = MainClassUtilities.getMainClassLocation(project);
        File destination = new File(getSourcesOutputDirectory(), mainClass.replace(".", "/").concat(".java"));
        if (!destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
        PluginMainClassGenerator generator = new PluginMainClassGenerator(project, packages, destination);
        generator.generate();
    }

    private File getSourceClassesFolder() {
        return new File(project.getBasedir(), "/target/classes/");
    }

    private File getGeneratedSourcesFolder() {
        return new File(project.getBasedir(), "/target/generated-sources/");
    }

    private Set<Artifact> getDependencyArtifacts() {
        Set<Artifact> artifacts = project.getArtifacts();
        return artifacts.stream()
                .filter(artifact -> VALID_SCOPES.contains(artifact.getScope()))
                .collect(Collectors.toSet());
    }
}
