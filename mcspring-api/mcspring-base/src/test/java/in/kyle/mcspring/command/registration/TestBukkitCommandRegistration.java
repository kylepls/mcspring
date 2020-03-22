package in.kyle.mcspring.command.registration;

import org.bukkit.command.Command;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import lombok.var;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

class TestBukkitCommandRegistration {
    
    @Test
    void testSetRequiredFields() throws NoSuchMethodException {
        // Tests that required command fields are copied into the command
        var controller = mock(CommandController.class);
        var command = mock(Command.class);
        var registration = new BukkitCommandRegistration(controller, (m, o, n) -> command);
        
        abstract class Test {
            @in.kyle.mcspring.command.Command(value = "command",
                                              aliases = {"a1", "a2", "a3"},
                                              description = "description",
                                              usage = "usage",
                                              permission = "permission",
                                              permissionMessage = "permissionMessage")
            abstract void command();
        }
        
        Method method = Test.class.getDeclaredMethod("command");
        var annotation = method.getAnnotation(in.kyle.mcspring.command.Command.class);
        
        registration.register(annotation, method, null);
        
        verify(command, atLeastOnce()).setAliases(asList("a1", "a2", "a3"));
        verify(command, atLeastOnce()).setDescription("description");
        verify(command, atLeastOnce()).setUsage("usage");
        verify(command, atLeastOnce()).setPermission("permission");
        verify(command, atLeastOnce()).setPermissionMessage("permissionMessage");
    }
}
