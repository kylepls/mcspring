package com.joshb.mcspringboot.event;

import com.joshb.mcspringboot.util.SpringScanner;

import org.bukkit.event.EventHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
class EventHandlerSupport implements ApplicationContextAware {
    
    private final EventService eventService;
    private final SpringScanner scanner;
    
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        scanner.scanMethods(EventHandler.class)
                .forEach((method, object) -> eventService.registerEvent(object, method));
    }
}
