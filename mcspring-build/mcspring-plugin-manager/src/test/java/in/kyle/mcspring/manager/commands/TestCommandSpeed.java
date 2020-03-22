package in.kyle.mcspring.manager.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import in.kyle.mcspring.test.command.TestSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
class TestCommandSpeed {
    
    @Autowired
    TestCommandExecutor executor;
    
    TestSender sender;
    
    @BeforeEach
    void setup() {
        sender = spy(TestSender.class);
    }
    
    @Test
    void testSpeed() {
        executor.run(sender, "speed 10");
        assertThat(sender.getMessages()).first().asString().matches("Speed set to [^ ]+");
        verify(sender).setFlySpeed(10);
        verify(sender).setWalkSpeed(10);
    }
    
    @Test
    void testSpeedUsage() {
        executor.run(sender, "speed");
        assertThat(sender.getMessages()).first().asString().startsWith("Usage: ");
        verify(sender, never()).setFlySpeed(anyFloat());
        verify(sender, never()).setWalkSpeed(anyFloat());
    }
}
