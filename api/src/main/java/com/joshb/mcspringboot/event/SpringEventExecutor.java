package com.joshb.mcspringboot.event;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import lombok.SneakyThrows;
import lombok.val;

@Component
public class SpringEventExecutor {
    
    public EventExecutor create(Object listenerBean, Method method) {
        return (listener, event) -> triggerEvent(method, listenerBean, event);
    }
    
    @SneakyThrows
    private void triggerEvent(Method method, Object listenerBean, Event event) {
        AopUtils.invokeJoinpointUsingReflection(listenerBean, method, new Object[]{event});
    }
}
