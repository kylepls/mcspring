package in.kyle.mcspring.autogenerator;


import com.google.common.collect.Sets;
import in.kyle.mcspring.annotation.PluginDepend;
import lombok.val;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestGenerateFilesGoal extends BetterAbstractMojoTestCase {

    private GenerateFilesMojo mojo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File testPom = new File(getBasedir(), "/src/test/resources/unit/basic-test/pom.xml");
        this.mojo = (GenerateFilesMojo) lookupConfiguredMojo(testPom, "generate-files");
        mojo.initializeClassLoader();
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
        File targetDir = TestingUtils.getTestProjectTargetDirectory(getBasedir());
        File generatedDir = new File(targetDir, "generated-sources/mc-spring");
        File mainClass = new File(generatedDir, "org/springframework/boot/loader/testprojectgroupid/testproject.java");
        File pluginYml = new File(generatedDir, "plugin.yml");
        assertTrue(generatedDir.exists());
        assertTrue(generatedDir.isDirectory());
        assertTrue(mainClass.exists());
        assertTrue(pluginYml.exists());
    }
}
