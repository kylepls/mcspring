package in.kyle.mcspring.manager.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@MCSpringTest
class TestCommandHeal {

    @Autowired
    TestCommandExecutor commandExecutor;
    
    @Test
    void testHeal() {
        List<String> output = commandExecutor.run("heal");
        assertThat(output).first().asString().matches("Healed [^ ]+");
    }
}
