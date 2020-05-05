package in.kyle.mcspring.autogenerator;

import java.io.File;

public class TestingUtils {

    static File getTestProjectTargetDirectory(String base) {
        return new File(base, "/src/test/resources/unit/basic-test/target/");
    }
}
