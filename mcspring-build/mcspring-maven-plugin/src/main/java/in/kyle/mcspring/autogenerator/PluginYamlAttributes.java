package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.autogenerator.util.MainClassUtilities;
import lombok.SneakyThrows;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class PluginYamlAttributes {

    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private final List<String> softDependencies = new ArrayList<>();
    private final List<String> requiredDependencies = new ArrayList<>();
    private final MavenProject project;
    private final List<PluginDepend> dependencies;

    PluginYamlAttributes(MavenProject project, List<PluginDepend> dependencies) {
        this.project = project;
        this.dependencies = dependencies;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void loadAttributes() {
        attributes.put("name", project.getName());
        attributes.put("main", MainClassUtilities.getMainClassLocation(project));
        attributes.put("version", project.getVersion());
        attributes.put("description", project.getDescription());
        loadDependencies();
    }

    private void loadDependencies() {
        loadSoftDependencies(dependencies);
        loadRequiredDependencies(dependencies);
        attributes.put("softdepend", softDependencies);
        attributes.put("depend", requiredDependencies);
    }

    private void loadSoftDependencies(List<PluginDepend> dependencies) {
        dependencies.stream().filter(PluginDepend::soft)
                .flatMap(pluginDepend -> Arrays.stream(pluginDepend.plugins()))
                .forEach(softDependencies::add);
    }

    private void loadRequiredDependencies(List<PluginDepend> dependencies) {
        dependencies.stream().filter(pluginDepend -> !pluginDepend.soft())
                .flatMap(pluginDepend -> Arrays.stream(pluginDepend.plugins()))
                .forEach(requiredDependencies::add);
    }


    @SneakyThrows
    public void writeToFile(File destination) {
        FileWriter writer = new FileWriter(destination);
        Yaml yaml = new Yaml();
        //Pretty sure #dump closes the writer automatically
        yaml.dump(attributes, writer);
    }
}
