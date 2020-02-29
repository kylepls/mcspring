package in.kyle.mcspring.layout;

import org.springframework.boot.loader.tools.Layout;
import org.springframework.boot.loader.tools.LayoutFactory;
import org.springframework.boot.loader.tools.LibraryScope;

import java.io.File;

public class McSpringLayoutFactory implements LayoutFactory {
    
    // required by spring-boot
    @SuppressWarnings("unused")
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
                if (scope == LibraryScope.COMPILE) {
                    return "BOOT-INF/lib/";
                } else {
                    return null;
                }
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
