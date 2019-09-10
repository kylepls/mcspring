package in.kyle.mcspring.testplugin;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestApi {
    
    public String getValue() {
        return "24";
    }
}
