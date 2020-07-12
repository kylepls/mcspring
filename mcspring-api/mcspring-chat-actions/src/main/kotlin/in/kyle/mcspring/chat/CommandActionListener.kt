package `in`.kyle.mcspring.chat

import `in`.kyle.mcspring.rx.observeEvent
import `in`.kyle.mcspring.rx.syncScheduler
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.TimeUnit

object CommandActionListener {

    private val actions = mutableMapOf<String, PublishSubject<Player>>()
    private val ttls = mutableMapOf<String, Long>()
    private var registered = false

    fun createCommand(): Pair<String, Observable<Player>> {
        if (!registered) {
            registerListener()
            registered = true
        }
        prune()

        val command = UUID.randomUUID().toString()
        val subject = PublishSubject.create<Player>()

        actions[command] = subject
        ttls[command] = System.currentTimeMillis()

        return Pair(command, subject.doOnDispose { unregisterCommand(command) })
    }

    fun unregisterCommand(string: String) {
        actions.remove(string)?.onComplete()
        ttls.remove(string)
    }

    private fun prune() {
        val toRemove = ttls.filterValues { (System.currentTimeMillis() - it) > TimeUnit.DAYS.toMillis(1) }
        toRemove.keys.forEach {
            unregisterCommand(it)
        }
    }

    private fun registerListener() {
        val plugin = JavaPlugin.getProvidingPlugin(CommandActionListener::class.java)
        plugin.observeEvent(PlayerCommandPreprocessEvent::class)
                .filter { it.message.startsWith("/") }
                .subscribe {
                    val command = it.message.substring(1)
                    val subject = actions[command]
                    if (subject != null) {
                        it.isCancelled = true
                        plugin.syncScheduler().scheduleDirect {
                            subject.onNext(it.player)
                        }
                    }
                }
    }
}
