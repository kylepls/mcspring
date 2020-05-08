package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.autogenerator.util.MainClassUtilities;
import in.kyle.mcspring.command.Command;
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
    private final List<Command> commands;

    PluginYamlAttributes(MavenProject project, List<PluginDepend> dependencies, List<Command> commands) {
        this.project = project;
        this.dependencies = dependencies;
        this.commands = commands;
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
        loadCommands();
    }

    private void loadDependencies() {
        loadSoftDependencies();
        loadRequiredDependencies();
        attributes.put("softdepend", softDependencies);
        attributes.put("depend", requiredDependencies);
    }

    private void loadCommands() {
        Map<String, Map<String, Object>> commandMap = new HashMap<>();
        commands.forEach(command -> commandMap.put(command.value(), commandToMap(command)));
        attributes.put("commands", commandMap);
    }

    private Map<String, Object> commandToMap(Command command) {
        Map<String, Object> map = new HashMap<>();
        map.put("description", command.description());
        map.put("aliases", command.aliases());
        map.put("permission", command.permission());
        map.put("permission-message", command.permissionMessage());
        map.put("usage", command.usage());
        return map;
    }

    private void loadSoftDependencies() {
        dependencies.stream().filter(PluginDepend::soft)
                .flatMap(pluginDepend -> Arrays.stream(pluginDepend.plugins()))
                .forEach(softDependencies::add);
    }

    private void loadRequiredDependencies() {
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
