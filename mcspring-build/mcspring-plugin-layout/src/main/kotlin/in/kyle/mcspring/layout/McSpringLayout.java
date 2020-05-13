package in.kyle.mcspring.layout;

import org.springframework.boot.loader.tools.CustomLoaderLayout;
import org.springframework.boot.loader.tools.Layout;
import org.springframework.boot.loader.tools.LibraryScope;
import org.springframework.boot.loader.tools.LoaderClassesWriter;

import java.io.IOException;

public class McSpringLayout implements Layout, CustomLoaderLayout {
    
    private static final String NESTED_LOADER_JAR = "META-INF/loader/mcspring-jar-loader.jar";
    
    @Override
    public String getLauncherClassName() {
        return "";
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
    
    @Override
    public void writeLoadedClasses(LoaderClassesWriter writer) throws IOException {
        writer.writeLoaderClasses();
        writer.writeLoaderClasses(NESTED_LOADER_JAR);
    }
}
