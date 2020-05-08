package testplugin;

import in.kyle.mcspring.annotation.PluginDepend;
import in.kyle.mcspring.command.Command;
import org.springframework.stereotype.Component;

@Component
public class ComponentWithCommands {

    @Command(
            value = "test",
            description = "test desc",
            usage = "/testplugin test",
            aliases = "t",
            permission = "plugin.command.test",
            permissionMessage = "Hell nah"
    )
    public void someMethod() {

    }
}