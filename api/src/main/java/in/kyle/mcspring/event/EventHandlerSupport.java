package in.kyle.mcspring.event;

import in.kyle.mcspring.util.SpringScanner;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.EventExecutor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.Method;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@Configuration
@AllArgsConstructor
class EventHandlerSupport implements ApplicationContextAware {
    
    private final EventService eventService;
    private final SpringScanner scanner;
    
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        scanner.scanMethods(EventHandler.class).forEach(this::register);
    }
    
    private void register(Method method, Object container) {
        eventService.registerEvent(method, createEventExecutor(container, method));
    }
    
    private EventExecutor createEventExecutor(Object listenerBean, Method method) {
        return (listener, event) -> triggerEvent(method, listenerBean, event);
    }
    
    @SneakyThrows
    private void triggerEvent(Method method, Object listenerBean, Event event) {
        AopUtils.invokeJoinpointUsingReflection(listenerBean, method, new Object[]{event});
    }
}
