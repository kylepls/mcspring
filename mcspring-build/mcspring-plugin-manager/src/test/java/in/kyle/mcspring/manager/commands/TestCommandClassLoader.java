package in.kyle.mcspring.manager.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@MCSpringTest
class TestCommandClassLoader {
    
    @Autowired
    TestCommandExecutor commandExecutor;
    
    @Test
    void testClassLoader() {
        List<String> output = commandExecutor.run("classloader " + getClass().getName());
        assertThat(output).hasSize(2);
        assertThat(output.get(0)).isEqualTo(
                "ClassLoader: " + getClass().getClassLoader().toString());
        assertThat(output.get(1)).isEqualTo("Domain: " + getClass().getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toString());
    }
}
