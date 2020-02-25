package in.kyle.mcspring.processor.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Set;
import java.util.stream.Collectors;

import javax.tools.FileObject;

public class MainClassCreator {
    public static void generateMain(FileObject main, String fqn, String packageName, Set<String> packages)
            throws IOException {
        InputStream resource = MainClassCreator.class.getResourceAsStream("/Main.java");
        try (Writer writer = main.openWriter()) {
            String template = IOUtils.toString(new InputStreamReader(resource));
            String name = fqn;
            if (fqn.contains(".")) {
                template = String.format("package %s;\n", packageName) + template;
                name = fqn.substring(fqn.lastIndexOf(".") + 1);
            }
            String scans = makeScanStrings(packages);
            writer.write(template.replace("{name}", name).replace("{scans}", scans));
        }
    }
    
    private static String makeScanStrings(Set<String> strings) {
        return strings.stream()
                .map(MainClassCreator::makeScanString)
                .collect(Collectors.joining("\n"));
    }
    
    private static String makeScanString(String s) {
        return "@ComponentScan(basePackages = \"{s}\")".replace("{s}", s);
    }
}
