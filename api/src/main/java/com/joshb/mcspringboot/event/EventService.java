package com.joshb.mcspringboot.event;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.val;

@Service
@AllArgsConstructor
public class EventService {
    
    private final SpringEventExecutor eventExecutor;
    private final Server server;
    private final Plugin plugin;
    
    void registerEvent(Object listenerBean, Method method) {
        val handler = method.getAnnotation(EventHandler.class);
        val eventType = (Class<? extends Event>) method.getParameters()[0].getType();
        
        server.getPluginManager().registerEvent(eventType,
                                                new Listener() {
                                                },
                                                handler.priority(),
                                                eventExecutor.create(listenerBean, method),
                                                plugin,
                                                handler.ignoreCancelled());
    }
}
