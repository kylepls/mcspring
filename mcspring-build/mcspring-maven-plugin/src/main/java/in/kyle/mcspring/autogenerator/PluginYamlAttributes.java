package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.annotation.PluginDepend;
import lombok.SneakyThrows;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class PluginYamlAttributes {

    private static final String NAME = "name";
    private static final String MAIN = "main";
    private static final String VERSION = "version";
    private static final String DESC = "description";
    private static final String SOFT_DEPENDENCIES = "softdepend";
    private static final String REQUIRED_DEPENDENCIES = "depend";

    private final Map<String, Object> attributes = new HashMap<>();
    private final MavenProject project;
    private final PluginDependencyResolver resolver;
    private final Log logger;

    PluginYamlAttributes(MavenProject project, PluginDependencyResolver resolver, Log logger) {
        this.project = project;
        this.resolver = resolver;
        this.logger = logger;
        attributes.put(SOFT_DEPENDENCIES, new ArrayList<>());
        attributes.put(REQUIRED_DEPENDENCIES, new ArrayList<>());
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void loadAttributes() {
        attributes.put(NAME, project.getName());
        attributes.put(MAIN, AutoGenerationPlugin.getMainClassLocation(project));
        attributes.put(VERSION, project.getVersion());
        attributes.put(DESC, project.getDescription());
        setDependencies();
    }

    private void setDependencies() {
        logger.info("Scanning dependency classes for PluginDepend annotation");
        List<PluginDepend> list = resolver.resolveAllDependencies();
        logger.info(String.format("Scan complete. Found %d classes with PluginDepend annotation", list.size()));
        list.forEach(this::addDependency);
    }

    private List<String> getDependencyList(String type) {
        return (List<String>) attributes.get(type);
    }

    private void addDependency(PluginDepend depend) {
        List<String> plugins = Arrays.stream(depend.plugins()).collect(Collectors.toList());
        if(depend.soft()) {
            getDependencyList(SOFT_DEPENDENCIES).addAll(plugins);
        } else {
            getDependencyList(REQUIRED_DEPENDENCIES).addAll(plugins);
        }
    }

    @SneakyThrows
    public void writeToFile(File destination) {
        FileWriter writer = new FileWriter(destination);
        Yaml yaml = new Yaml();
        //Pretty sure #dump closes the writer automatically
        yaml.dump(attributes, writer);
    }
}
