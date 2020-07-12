package `in`.kyle.mcspring.guis

import `in`.kyle.mcspring.guis.chat.ChatGui
import `in`.kyle.mcspring.guis.chat.ChatGuiDrawer
import `in`.kyle.mcspring.guis.chat.ChatGuiSetup
import `in`.kyle.mcspring.guis.hotbar.HotbarDrawer
import `in`.kyle.mcspring.guis.hotbar.HotbarGui
import `in`.kyle.mcspring.guis.hotbar.HotbarSetup
import `in`.kyle.mcspring.guis.invenotry.InventoryDrawer
import `in`.kyle.mcspring.guis.invenotry.InventoryGui
import `in`.kyle.mcspring.guis.invenotry.InventorySetup
import `in`.kyle.mcspring.guis.invenotry.InventorySize
import `in`.kyle.mcspring.rx.observeEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object Gui {

    val plugin = JavaPlugin.getProvidingPlugin(InventoryGui::class.java)
    private val currentGuis = mutableMapOf<Player, ClickableGui<out Any, out Any>>()

    fun inventory(
            player: Player,
            title: String,
            size: InventorySize = InventorySize.ONE_LINE,
            parent: ClickableGui<out Any, out Any>? = null,
            lambda: GuiBuilder<InventorySetup, InventoryDrawer>.() -> Unit
    ): Disposable {
        require(parent?.isDisabled() ?: true) { "Cannot open GUIs over each other." }
        val gui = InventoryGui(
                size.slots,
                title,
                plugin,
                player,
                parent
        )
        return setup(gui, lambda)
    }

    fun inventoryWithParent(
            player: Player,
            title: String,
            size: InventorySize = InventorySize.ONE_LINE,
            lambda: GuiBuilder<InventorySetup, InventoryDrawer>.() -> Unit
    ) = inventory(player, title, size, currentGuis[player], lambda)

    fun hotbar(
            player: Player,
            parent: ClickableGui<out Any, out Any>? = null,
            lambda: GuiBuilder<HotbarSetup, HotbarDrawer>.() -> Unit
    ): Disposable {
        require(parent?.isDisabled() ?: true) { "Cannot open GUIs over each other." }
        val gui = HotbarGui(plugin, player, parent)
        return setup(gui, lambda)
    }

    fun hotbarWithParent(
            player: Player,
            lambda: GuiBuilder<HotbarSetup, HotbarDrawer>.() -> Unit
    ) = hotbar(player, currentGuis[player], lambda)

    fun chat(
            player: Player,
            parent: ClickableGui<out Any, out Any>? = null,
            lambda: GuiBuilder<ChatGuiSetup, ChatGuiDrawer>.() -> Unit
    ): Disposable {
        require(parent?.isDisabled() ?: true) { "Cannot open GUIs over each other." }
        val gui = ChatGui(plugin, player, parent)
        return setup(gui, lambda)
    }

    fun chatWithParent(
            player: Player,
            lambda: GuiBuilder<ChatGuiSetup, ChatGuiDrawer>.() -> Unit
    ) = chat(player, currentGuis[player], lambda)

    @Suppress("UNCHECKED_CAST")
    private fun <Drawer, Setup> setup(
            gui: ClickableGui<Drawer, Setup>,
            lambda: GuiBuilder<Setup, Drawer>.() -> Unit
    ): Disposable {
        val builder = GuiBuilder<Setup, Drawer>()
        builder.lambda()
        builder.setup?.apply { gui.setupLambdas.add(this) }
        builder.redraw?.apply { gui.redrawLambdas.add(this) }
        currentGuis[gui.player] = gui as ClickableGui<out Any, out Any>
        val sub = gui.open()
        gui.globalSubscription.add(Disposable.fromRunnable {
            if (!gui.isDisabled()) gui.disable()
            currentGuis.remove(gui.player)
            gui.parent?.apply {
                enable()
                currentGuis[player] = this
            }
        })
        return sub
    }
}

@Target(AnnotationTarget.CLASS)
annotation class DslMark

@DslMark
class GuiBuilder<Setup, Drawer> {
    internal var setup: (Setup.() -> Unit)? = null
    internal var redraw: (Drawer.() -> Unit)? = null

    fun setup(lambda: Setup.() -> Unit) {
        setup = lambda
    }

    fun redraw(lambda: Drawer.() -> Unit) {
        redraw = lambda
    }
}

abstract class ClickableGui<Drawer, Setup>(
        val plugin: Plugin,
        val player: Player,
        val parent: ClickableGui<out Any, out Any>?
) {

    lateinit var globalSubscription: CompositeDisposable
    lateinit var listenerSubscription: CompositeDisposable

    val redrawLambdas = mutableSetOf<Drawer.() -> Unit>()
    val setupLambdas = mutableSetOf<Setup.() -> Unit>()

    protected abstract fun registerListeners(): CompositeDisposable
    protected abstract fun makeDrawer(): Drawer
    protected abstract fun makeSetup(): Setup

    abstract fun clear()

    open fun redraw() {
        clear()
        redrawLambdas.forEach { it(makeDrawer()) }
    }

    open fun open(): Disposable {
        require(!this::globalSubscription.isInitialized) { "GUI can only be opened once" }
        require(parent?.isDisabled()
                ?: true) { "Parent must be disabled/closed before a new GUI can be opened" }
        globalSubscription = makeDefaultSubscription()
        enable()
        return globalSubscription
    }

    fun isDisabled() = listenerSubscription.isDisposed

    open fun close() {
        require(!globalSubscription.isDisposed) { "GUI already closed" }
        globalSubscription.dispose()
    }

    open fun enable() {
        require(this::globalSubscription.isInitialized) { "GUI enable called before open" }
        require(!this::listenerSubscription.isInitialized || listenerSubscription.isDisposed) {
            "Cannot enable, already enabled"
        }
        listenerSubscription = registerListeners()
        setupLambdas.forEach { it(makeSetup()) }
        redraw()
    }

    open fun disable() {
        require(this::globalSubscription.isInitialized) { "GUI disable called before open" }
        require(!listenerSubscription.isDisposed) { "Cannot disable, already disabled" }
        listenerSubscription.dispose()
        globalSubscription.remove(listenerSubscription)
        clear()
    }

    private fun makeDefaultSubscription(): CompositeDisposable {
        val subscription = CompositeDisposable()

        subscription.add(plugin.observeEvent(PlayerQuitEvent::class)
                .filter { it.player == player }
                .subscribe { subscription.dispose() })

        subscription.add(plugin.observeEvent(PluginDisableEvent::class)
                .filter { it.plugin == plugin }
                .subscribe { subscription.dispose() })

        return subscription
    }
}

open class ClickContext(val gui: ClickableGui<*, *>) {
    val listenerSubscription: CompositeDisposable = gui.listenerSubscription
    val globalSubscription: CompositeDisposable = gui.globalSubscription
}
