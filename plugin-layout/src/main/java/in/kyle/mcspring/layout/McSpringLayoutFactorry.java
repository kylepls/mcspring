package in.kyle.mcspring.layout;

import org.springframework.boot.loader.tools.CustomLoaderLayout;
import org.springframework.boot.loader.tools.JarWriter;
import org.springframework.boot.loader.tools.Layout;
import org.springframework.boot.loader.tools.LayoutFactory;
import org.springframework.boot.loader.tools.LibraryScope;
import org.springframework.boot.loader.tools.LoaderClassesWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class McSpringLayoutFactorry implements LayoutFactory {
    
    private static final String NESTED_LOADER_JAR = "META-INF/loader/spring-boot-loader.jar";
    
    private String name = "mcspring";
    
    @Override
    public Layout getLayout(File file) {
        return new Layout() {
            
            @Override
            public String getLauncherClassName() {
                return "org.springframework.boot.loader.JarLauncher";
            }
            
            @Override
            public String getLibraryDestination(String libraryName, LibraryScope scope) {
                return "BOOT-INF/lib/";
            }
            
            @Override
            public String getClassesLocation() {
                return "";
            }
            
            @Override
            public boolean isExecutable() {
                return true;
            }
        };
    }
}
