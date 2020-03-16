package in.kyle.mcspring.event;

import org.bukkit.event.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanInitializationException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

class TestEventService {
    
    EventService eventService;
    
    @BeforeEach
    void setup() {
        eventService = new EventService(null, null);
    }
    
    @Test
    void testRegisterConditions() throws NoSuchMethodException {
        class Test {
            @EventHandler
            void noParams() {
            }
            
            @EventHandler
            void invalidParam(int i) {
            }
        }
    
        Method noParams = Test.class.getDeclaredMethod("noParams");
        assertThatExceptionOfType(BeanInitializationException.class).isThrownBy(() -> {
            eventService.registerEvent(noParams, null);
        });
    
        Method invalidParam = Test.class.getDeclaredMethod("invalidParam", int.class);
        assertThatExceptionOfType(BeanInitializationException.class).isThrownBy(() -> {
            eventService.registerEvent(invalidParam, null);
        });
    }
}
