package `in`.kyle.mcspring.guis.chat

import `in`.kyle.mcspring.guis.ClickContext
import `in`.kyle.mcspring.guis.ClickableGui
import `in`.kyle.mcspring.rx.observeEvent
import `in`.kyle.mcspring.rx.syncScheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.Plugin

class ChatGui(
    plugin: Plugin,
    player: Player,
    parent: ClickableGui<out Any, out Any>?
) : ClickableGui<ChatGuiDrawer, ChatGuiSetup>(plugin, player, parent) {

    val actions = mutableListOf<(String) -> Unit>()

    override fun registerListeners(): CompositeDisposable {
        val subscription = CompositeDisposable()
        subscription.add(plugin.observeEvent(AsyncPlayerChatEvent::class)
            .filter { it.player == player }
            .observeOn(plugin.syncScheduler())
            .subscribeOn(plugin.syncScheduler())
            .subscribe { event ->
                actions.toList().forEach { it(event.message) }
            })
        return subscription
    }

    override fun makeDrawer() = ChatGuiDrawer(this)

    override fun makeSetup() = ChatGuiSetup(this)

    override fun clear() {
        actions.clear()
        repeat(100) {
            player.sendMessage(" ")
        }
    }
}

open class ChatGuiDrawer(val chat: ChatGui) : ClickContext(chat) {

    fun message(lambda: (String) -> Unit) {
        chat.actions.add(lambda)
    }

    fun redraw() = chat.redraw()
    fun close() = chat.close()
    fun enable() = chat.enable()
    fun disable() = chat.disable()
}

class ChatGuiSetup(chat: ChatGui) : ChatGuiDrawer(chat)
