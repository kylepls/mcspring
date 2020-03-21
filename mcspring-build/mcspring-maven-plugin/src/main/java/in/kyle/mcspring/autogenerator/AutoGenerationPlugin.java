package in.kyle.mcspring.autogenerator;

import lombok.SneakyThrows;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mojo(name = "mcspring-auto-generator", defaultPhase = LifecyclePhase.TEST,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class AutoGenerationPlugin extends AbstractMojo {

    public static final String ROOT_PACKAGE = "org.springframework.boot.loader";
    private static final List<String> VALID_SCOPES = Arrays.asList("provided", "compile", "runtime");
    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    public static String getMainClassLocation(MavenProject project) {
        String groupId = project.getGroupId().replace("-", "");
        String artifactId = project.getArtifactId().replace("-", "");
        return AutoGenerationPlugin.ROOT_PACKAGE + "." + groupId + "." + artifactId;
    }

    @SneakyThrows
    public void execute() {
        preparePluginYml();
        preparePluginMainClass();
    }

    private void preparePluginYml() {
        getLog().info("Scanning for project dependencies in qualifying scope");
        Set<Artifact> artifacts = getDependencyArtifacts();
        getLog().info(String.format("Dependency scan complete. Found %d dependencies", artifacts.size()));
        PluginDependencyResolver resolver = new PluginDependencyResolver(artifacts);
        PluginYamlAttributes attributes = new PluginYamlAttributes(project, resolver, getLog());
        attributes.loadAttributes();
        getLog().info("Finished obtaining data for plugin.yml");
        getLog().info("=======================================");
        attributes.getAttributes().forEach((key, data) -> getLog().info(key + ": " + data.toString()));
        getLog().info("=======================================");
        getLog().info("Writing plugin.yml to generated-sources");
        File pluginFile = new File(getGeneratedSourcesFolder(), "plugin.yml");
        attributes.writeToFile(pluginFile);
        getLog().info("Write completed");
    }

    private void preparePluginMainClass() {
        PluginDependencyResolver resolver = new PluginDependencyResolver(getDependencyArtifacts());
        getLog().info("Scanning project sources for spring annotations");
        ProjectClassScanner scanner = new ProjectClassScanner(getSourceClassesFolder(), resolver.getDependencyURLs());
        scanner.findPackages();
        Set<String> packages = scanner.getPackagesThatUseSpring();
        getLog().info(String.format("Scan complete. Found %d packages with spring annotation", packages.size()));
        getLog().info("Preparing to generate main class");
        File destination = new File(getGeneratedSourcesFolder(), getMainClassLocation(project).replace(".", "/").concat(".java"));
        destination.getParentFile().mkdirs();
        PluginMainClassGenerator generator = new PluginMainClassGenerator(project.getName().replace("-", ""), packages, destination);
        generator.generate();
        getLog().info("Default main class has been added to generated-sources");
        getLog().info("Auto generation process complete");
    }

    private File getSourceClassesFolder() {
        return new File(project.getBasedir(), "/target/classes/");
    }

    private File getGeneratedSourcesFolder() {
        return new File(project.getBasedir(), "/target/generated-sources/");
    }

    @SuppressWarnings("unchecked")
    private Set<Artifact> getDependencyArtifacts() {
        Set<Artifact> artifacts = project.getArtifacts();
        return artifacts.stream()
                .filter(artifact -> VALID_SCOPES.contains(artifact.getScope()))
                .collect(Collectors.toSet());
    }
}
