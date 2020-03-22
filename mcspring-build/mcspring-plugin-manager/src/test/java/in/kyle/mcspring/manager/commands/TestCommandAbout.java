package in.kyle.mcspring.manager.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;

import static org.assertj.core.api.Assertions.*;

@MCSpringTest
class TestCommandAbout {
    
    @Autowired
    TestCommandExecutor commandExecutor;
    
    @Test
    void testAbout() {
        List<String> output = commandExecutor.run("about");
        assertThat(output).hasSize(4);
        assertThat(output.get(0)).matches("Plugin Name: [^ ]+");
        assertThat(output.get(1)).matches("Plugin Version: [^ ]+");
        assertThat(output.get(2)).matches("Spring Version: [^ ]+");
        assertThat(output.get(3)).matches("Bukkit Version: [^ ]+");
    }
}
