package in.kyle.mcspring.autogenerator;

import java.io.File;

public class TestGenerateFilesGoal extends BetterAbstractMojoTestCase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testGeneratedFiles() throws Exception {
        File testPom = new File(getBasedir(), "/src/test/resources/unit/basic-test/pom.xml");
        GenerateFilesMojo mojo = (GenerateFilesMojo) lookupConfiguredMojo(testPom, "generate-files");
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
