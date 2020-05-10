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

@Component
@ConditionalOnBean(Plugin::class)
internal class EventHandlerSupport(
        private val eventService: EventService,
        private val scanner: SpringScanner
) : ApplicationContextAware {

    override fun setApplicationContext(ctx: ApplicationContext) {
        scanner.scanMethods(EventHandler::class.java).forEach {
            val executor = EventExecutor { _: Listener, event: Event ->
                AopUtils.invokeJoinpointUsingReflection(it.value, it.key, arrayOf(event))
            }
            eventService.registerEvent(it.key, executor)
        }
    }
}
