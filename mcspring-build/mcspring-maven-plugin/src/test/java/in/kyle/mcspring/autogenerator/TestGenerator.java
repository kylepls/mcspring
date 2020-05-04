package in.kyle.mcspring.autogenerator;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class TestGenerator extends AbstractMojoTestCase {
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testMojoGoal() throws Exception {
        File testPom = new File(getBasedir(),
                                "src/test/resources/unit/basic-test/basic-test-plugin-config.xml");
        GenerateFilesMojo mojo = (GenerateFilesMojo) lookupEmptyMojo("generate-files", testPom);
        
        assertNotNull(mojo);
        mojo.execute();
    }
}
