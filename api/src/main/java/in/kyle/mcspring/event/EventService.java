package in.kyle.mcspring.event;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.val;

@Service
@AllArgsConstructor
@Profile("!test")
class EventService {
    
    private final Server server;
    private final Plugin plugin;
    
    void registerEvent(Method method, EventExecutor executor) {
        val handler = method.getAnnotation(EventHandler.class);
        val eventType = (Class<? extends Event>) method.getParameters()[0].getType();
        
        server.getPluginManager()
                .registerEvent(eventType,
                               makeListener(),
                               handler.priority(),
                               executor,
                               plugin,
                               handler.ignoreCancelled());
    }
    
    private Listener makeListener() {
        return new Listener() {
        };
    }
}
