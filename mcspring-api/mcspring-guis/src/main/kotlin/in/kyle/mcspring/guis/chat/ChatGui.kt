package `in`.kyle.mcspring.guis.chat

import `in`.kyle.mcspring.guis.GuiBase
import `in`.kyle.mcspring.rx.observeEvent
import `in`.kyle.mcspring.rx.syncScheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.Plugin

abstract class ChatGui(
        plugin: Plugin,
        player: Player,
        parent: GuiBase? = null
) : GuiBase(plugin, player, parent) {

    abstract fun onMessage(message: String)

    override fun registerListeners(): CompositeDisposable {
        val subscription = CompositeDisposable()
        subscription.add(plugin.observeEvent(AsyncPlayerChatEvent::class)
                .filter { it.player == player }
                .doOnNext { it.isCancelled = true }
                .observeOn(plugin.syncScheduler())
                .subscribeOn(plugin.syncScheduler())
                .map { it.message }
                .subscribe(::onMessage))
        return subscription
    }

    override fun clear() {
        repeat(101) {
            player.sendMessage(" ")
        }
    }
}
