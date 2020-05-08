package in.kyle.mcspring.manager;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@SpringBootConfiguration
public class TestSpringSupport {
    @Bean
    Server server() throws Exception {
        Server server = mock(Server.class);
        
        // Adding a logging call on a setter is some big brain shit
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, server);
        
        return server;
    }
}
