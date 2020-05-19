package in.kyle.mcspring.autogenerator;


import com.google.common.collect.Sets;
import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.command.Command;
import lombok.val;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestGenerateFilesGoal extends BetterAbstractMojoTestCase {

    private GenerateFilesMojo mojo;
    private File targetDirectory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.targetDirectory = new File(getBasedir(), "/src/test/resources/unit/basic-test/target/");
        File testPom = new File(getBasedir(), "/src/test/resources/unit/basic-test/pom.xml");
        this.mojo = (GenerateFilesMojo) lookupConfiguredMojo(testPom, "generate-files");
        mojo.initializeClassLoader();
    }

    /*
    Tests to make sure the plugin yml content is accurate
     */
    public void testPluginYamlAttributes() {
        val attributes = mojo.getLoadedYamlAttributes();
        Map<String, Object> map = attributes.getAttributes();
        assertEquals(map.get("name"), "test-project");
        assertEquals(map.get("main"), "org.springframework.boot.loader.testprojectgroupid.testproject");
        assertEquals(((List) map.get("depend")).size(), 1);
        assertEquals(((List) map.get("softdepend")).size(), 0);
    }

    /*
    Tests command resolution
     */
    public void testCommandScanner() {
        val scanner = mojo.getCommandAnnotationScanner();
        List<Command> scanned = scanner.getCommandAnnotations();
        assertEquals(1, scanned.size());
    }

    /*
    Tests the resolution of all packages with spring annotations
     */
    public void testProjectSourceAnnotationScanner() {
        Set<String> expected = Sets.newHashSet("testplugin");
        val scanner = mojo.getSourceAnnotationScanner();
        scanner.findPackagesThatUseSpring();
        Set<String> actual = scanner.getPackagesThatUseSpring();
        assertEquals(expected, actual);
    }

    /*
    Tests the resolution of all dependencies (even transitive) found in the project with the @PluginDepend annotation
     */
    public void testDependencyResolution() {
        val resolver = mojo.getDependencyResolver();
        List<String> resolved = resolver.resolveAllDependencies()
                .stream()
                .map(PluginDepend::plugins)
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
        assertEquals(1, resolved.size());
        assertEquals("test-depend", resolved.get(0));
    }

    /*
    Tests that files are output to the correct directory
     */
    public void testGeneratedFiles() {
        assertNotNull(mojo);
        mojo.execute();
        File generatedDir = new File(targetDirectory, "generated-sources/mc-spring");
        File mainClass = new File(generatedDir, "org/springframework/boot/loader/testprojectgroupid/testproject.java");
        File pluginYml = new File(generatedDir, "plugin.yml");
        assertTrue(generatedDir.exists());
        assertTrue(generatedDir.isDirectory());
        assertTrue(mainClass.exists());
        assertTrue(pluginYml.exists());
    }
}
