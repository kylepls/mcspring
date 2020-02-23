package in.kyle.mcspring.processor.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import javax.tools.FileObject;

public class MainClassCreator {
    public static void generateMain(FileObject main, String className) throws IOException {
        InputStream resource = MainClassCreator.class.getResourceAsStream("/Main.txt");
        try (Writer writer = main.openWriter()) {
            String template = IOUtils.toString(new InputStreamReader(resource));
            String name;
            if (className.contains(".")) {
                String packageName = className.substring(0, className.lastIndexOf("."));
                template = String.format("package %s;\n", packageName) + template;
                name = className.substring(className.lastIndexOf(".") + 1);
            } else {
                name = className;
            }
            writer.write(template.replace("{name}", name));
        }
    }
}
