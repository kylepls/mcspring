package in.kyle.mcspring.manager.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.api.bukkit.entity.TestPlayer;
import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@MCSpringTest
class TestCommandSpeed {
    
    @Autowired
    TestCommandExecutor executor;
    
    @Autowired
    TestPlayer sender;
    
    @Test
    void testSpeed() {
        List<String> messages = executor.run(sender, "speed 10");
        assertThat(messages).first().asString().matches("Speed set to [^ ]+");
        assertThat(sender.getWalkSpeed()).isEqualTo(10F);
        assertThat(sender.getFlySpeed()).isEqualTo(10F);
    }
    
    @Test
    void testSpeedUsage() {
        sender.setWalkSpeed(0);
        sender.setFlySpeed(0);
        List<String> messages = executor.run(sender, "speed");
        assertThat(messages).first().asString().startsWith("Usage: ");
        assertThat(sender.getWalkSpeed()).isEqualTo(0);
        assertThat(sender.getFlySpeed()).isEqualTo(0);
    }
}
