package `in`.kyle.mcspring.event

import org.bukkit.Server
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.lang.reflect.Method

@Lazy
@Service
@ConditionalOnBean(Plugin::class)
internal class EventService(
        private val server: Server,
        private val plugin: Plugin
) {
    fun registerEvent(method: Method, executor: EventExecutor) {
        val handler = method.getAnnotation(EventHandler::class.java)

        @Suppress("UNCHECKED_CAST")
        val eventType = method.parameters[0].type as Class<out Event>
        server.pluginManager
                .registerEvent(eventType,
                        object : Listener {},
                        handler.priority,
                        executor,
                        plugin,
                        handler.ignoreCancelled)
    }
}
