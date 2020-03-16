package in.kyle.mcspring.command.registration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import in.kyle.mcspring.command.Command;
import in.kyle.mcspring.util.SpringScanner;
import lombok.var;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = TestCommandScanner.Config.class)
@Import({SpringScanner.class, CommandScanner.class, TestCommandScanner.TestCommand.class})
class TestCommandScanner {
    
    @Autowired
    CommandScanner scanner;
    
    @Test
    void test() {
        var commands = scanner.getRegisteredCommands();
        assertThat(commands).containsExactly(TestCommand.class.getDeclaredMethods()[0]);
    }
    
    @Component
    static class TestCommand {
        @Command("testCommand")
        void testCommand() {
        }
    }
    
    @SpringBootConfiguration
    static class Config {
        @Bean
        CommandRegistration commandRegistration() {
            return ((command, method, object) -> {
            });
        }
    }
}
