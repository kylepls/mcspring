package in.kyle.mcspring.command.registration;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import in.kyle.mcspring.command.SimpleMethodInjection;
import in.kyle.mcspring.command.registration.SimpleCommandFactory;
import lombok.var;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.mockito.Mockito.*;

class TestSimpleCommandFactory {
    
    SimpleCommandFactory factory;
    
    @BeforeEach
    void setup() {
        var injection = new SimpleMethodInjection(emptyList());
        factory = new SimpleCommandFactory(injection, emptySet(), mock(Plugin.class));
    }
    
    @Test
    void testExecutorInvokable() throws NoSuchMethodException {
        class Test {
            String test() {
                return "Hello World";
            }
        }
        
        Method method = Test.class.getDeclaredMethod("test");
        var command = factory.makeExecutor(method, new Test());
        var sender = mock(CommandSender.class);
        
        command.onCommand(sender, mock(Command.class), "", new String[0]);
        verify(sender, times(1)).sendMessage("Hello World");
    }
}
