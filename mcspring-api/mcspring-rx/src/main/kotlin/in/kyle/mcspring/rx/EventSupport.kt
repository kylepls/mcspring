package `in`.kyle.mcspring.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

fun <T : Event> Plugin.observeEvent(
        vararg classes: KClass<out T>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoredCanceled: Boolean = false
): Observable<T> {
    val listener = object : Listener {}
    return Observable.create { subscriber ->
        val executor = createExecutor(
                subscriber,
                classes.toList()
        )

        val pluginManager = Bukkit.getPluginManager()
        classes.forEach {
            pluginManager.registerEvent(it.java, listener, priority, executor, this, ignoredCanceled)
        }

        registerDisable(subscriber, this, listener)
        subscriber.setDisposable(Disposable.fromRunnable {
            HandlerList.unregisterAll(listener)
        })
    }
}

private fun <T : Event> createExecutor(
        subscriber: ObservableEmitter<T>,
        classes: List<KClass<out T>>
): EventExecutor {
    return EventExecutor { _, event ->
        if (classes.all { it.java.isAssignableFrom(event::class.java) }) {
            subscriber.onNext(event as T)
        }
    }
}

private fun registerDisable(
        subscriber: ObservableEmitter<*>,
        plugin: Plugin,
        listener: Listener
) {
    Bukkit.getPluginManager().registerEvent(
            PluginDisableEvent::class.java,
            listener,
            EventPriority.MONITOR,
            { _, event ->
                if ((event as PluginDisableEvent).plugin == plugin) {
                    subscriber.onComplete()
                }
            },
            plugin
    )
}
