package in.kyle.mcspring.layout;

import org.springframework.boot.loader.tools.Layout;
import org.springframework.boot.loader.tools.LayoutFactory;

import java.io.File;

public class McSpringLayoutFactory implements LayoutFactory {
    
    // required by spring-boot
    @SuppressWarnings("unused")
    private String name = "mcspring";
    
    @Override
    public Layout getLayout(File file) {
        return new McSpringLayout();
    }
}
