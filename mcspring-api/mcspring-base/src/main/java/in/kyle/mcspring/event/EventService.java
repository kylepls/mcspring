package in.kyle.mcspring.event;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import in.kyle.mcspring.RequiresSpigot;
import lombok.AllArgsConstructor;
import lombok.val;

@Service
@AllArgsConstructor
@RequiresSpigot
class EventService {
    
    private final Server server;
    private final Plugin plugin;
    
    void registerEvent(Method method, EventExecutor executor) {
        val handler = method.getAnnotation(EventHandler.class);
        val parameters = method.getParameters();
        
        if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0].getType())) {
            val eventType = (Class<? extends Event>) parameters[0].getType();
            server.getPluginManager()
                    .registerEvent(eventType,
                                   makeListener(),
                                   handler.priority(),
                                   executor,
                                   plugin,
                                   handler.ignoreCancelled());
        } else {
            throw new BeanInitializationException(String.format(
                    "Cannot load @EventHandler method %s, requires 1 parameter <? extends Event>",
                    method));
        }
    }
    
    private Listener makeListener() {
        return new Listener() {
        };
    }
}
