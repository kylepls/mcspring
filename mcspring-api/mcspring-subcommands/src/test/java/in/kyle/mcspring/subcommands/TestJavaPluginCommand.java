package in.kyle.mcspring.subcommands;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import in.kyle.mcspring.subcommands.plugincommand.api.PluginCommand;
import in.kyle.mcspring.test.MCSpringTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
public class TestJavaPluginCommand {
    
    @Autowired
    TestConsole console;
    
    Player sender;
    List<String> messages;
    
    @BeforeEach
    void setup() {
        messages = new ArrayList<>();
        sender = mock(Player.class);
        doAnswer(invocationOnMock -> {
            messages.add(invocationOnMock.getArgument(0));
            return null;
        }).when(sender).sendMessage(anyString());
    }
    
    @Test
    public void testSimple() {
        class Test {
            void parse(PluginCommand command) {
                command.withString();
                command.then(this::parse2);
            }
            
            String parse2(PluginCommand command, String parsedPart) {
                assertThat(command).isNotNull();
                return String.format("part is %s", parsedPart);
            }
        }
        
        console.run("test-arg another-arg", new Test()::parse, true, mock(Player.class));
        assertThat(messages).containsExactly("part is test-arg true");
    }
}
