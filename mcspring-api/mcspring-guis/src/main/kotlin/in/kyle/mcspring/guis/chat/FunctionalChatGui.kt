package `in`.kyle.mcspring.guis.chat

import `in`.kyle.mcspring.guis.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class FunctionalChatGui(
        plugin: Plugin,
        player: Player,
        parent: GuiBase?
) : FunctionalGui<ChatGuiDrawer, ChatGuiSetup, ChatGui>(player, plugin) {

    override val gui: ChatGui = object : ChatGui(plugin, player, parent) {
        override fun onMessage(message: String) = messageLambdas.forEach { it(message) }
        override fun setup() = runSetupLambdas()
        override fun redraw() = runRedrawLambdas()
    }

    internal val messageLambdas = mutableListOf<(String) -> Unit>()

    override fun makeDrawer() = ChatGuiDrawer(this)

    override fun makeSetup() = ChatGuiSetup(this)

}

open class ChatGuiDrawer(private val chat: FunctionalChatGui): FunctionalGuiDrawer {

    override val plugin = chat.plugin
    override val player = chat.player

    fun message(lambda: (String) -> Unit) {
        chat.messageLambdas.add(lambda)
    }

    fun clear() = chat.gui.clear()
    fun redraw() = chat.gui.redraw()
    fun close() = chat.gui.close()
    fun enable() = chat.gui.enable()
    fun disable() = chat.gui.disable()

}

class ChatGuiSetup(chat: FunctionalChatGui) : ChatGuiDrawer(chat), FunctionalGuiSetup {
    override val listenerSubscription = chat.gui.listenerSubscription
    override val globalSubscription = chat.gui.globalSubscription
}

fun Gui.chat(
        player: Player,
        lambda: GuiBuilder<ChatGuiSetup, ChatGuiDrawer>.() -> Unit
): Disposable {
    val functionalChatGui = FunctionalChatGui(plugin, player, getActiveGui(player))
    return setup(functionalChatGui, lambda)
}
