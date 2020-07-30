package `in`.kyle.mcspring.event

import `in`.kyle.mcspring.util.SpringScanner
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.springframework.aop.support.AopUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
internal class EventHandlerSupport(
        private val eventService: EventService,
        private val scanner: SpringScanner
) : ApplicationContextAware {

    override fun setApplicationContext(ctx: ApplicationContext) {
        scanner.scanMethods(EventHandler::class.java).forEach {
            val executor = makeExecutor(it.key, it.value)
            eventService.registerEvent(it.key, executor)
        }
    }

    private fun makeExecutor(method: Method, obj: Any): EventExecutor {
        return EventExecutor { _: Listener, event: Event ->
            AopUtils.invokeJoinpointUsingReflection(obj, method, arrayOf(event))
        }
    }
}
