package in.kyle.mcspring.testplugin;

import org.springframework.stereotype.Component;

@Component
public class TestApi {
    
    public String getValue() {
        return "24";
    }
}
