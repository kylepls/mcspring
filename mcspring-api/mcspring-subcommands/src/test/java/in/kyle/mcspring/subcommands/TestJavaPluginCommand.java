package in.kyle.mcspring.subcommands;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import in.kyle.mcspring.command.SimpleMethodInjection;
import in.kyle.mcspring.subcommands.plugincommand.PluginCommandImpl;
import in.kyle.mcspring.subcommands.plugincommand.api.PluginCommand;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestJavaPluginCommand {
    
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
        
        PluginCommandImpl pluginCommand =
                new PluginCommandImpl(new SimpleMethodInjection(Collections.emptyList()),
                                      sender,
                                      Arrays.asList("test-arg", "another-arg"),
                                      true);
        new Test().parse(pluginCommand);
        assertThat(messages).containsExactly("part is test-arg");
    }
}
