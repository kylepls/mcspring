package in.kyle.mcspring.autogenerator;

import in.kyle.mcspring.autogenerator.util.MainClassUtilities;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PluginMainClassGenerator {

    private final MavenProject project;
    private final Set<String> packages;
    private final File destination;

    public void generate() {
        String templateContent = getTemplateContent();
        templateContent = templateContent.replace("{name}", MainClassUtilities.getMainClassName(project));
        templateContent = templateContent.replace("{scans}", createPackageScanList());
        write(templateContent);
    }

    private String createPackageScanList() {
        return packages.stream()
                .map(s -> String.format("\"%s\"", s))
                .collect(Collectors.joining(",\n    "));
    }

    @SneakyThrows
    private void write(String completedTemplate) {
        FileWriter writer = new FileWriter(destination);
        writer.write(completedTemplate);
        writer.flush();
        writer.close();
    }

    private String getTemplateContent() {
        InputStream resource = PluginMainClassGenerator.class.getResourceAsStream("/MainTemplate.java");
        InputStreamReader inputStreamReader = new InputStreamReader(resource);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        builder.append("package ")
                .append(MainClassUtilities.getMainClassPackage(project))
                .append(";")
                .append("\n");
        bufferedReader.lines().forEach(line -> builder.append(line).append("\n"));
        return builder.toString();
    }
}
