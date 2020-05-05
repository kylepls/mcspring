package testplugin;

import in.kyle.mcspring.annotation.PluginDepend;
import org.springframework.stereotype.Component;

@Component
@PluginDepend(plugins = {"test-depend"})
public class ComponentWithDependency {

}