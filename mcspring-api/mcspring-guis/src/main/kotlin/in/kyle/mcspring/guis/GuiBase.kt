package `in`.kyle.mcspring.guis

import `in`.kyle.mcspring.guis.chat.ChatGui
import `in`.kyle.mcspring.guis.hotbar.HotbarGui
import `in`.kyle.mcspring.guis.inventory.InventoryGui
import `in`.kyle.mcspring.guis.inventory.InventorySize
import `in`.kyle.mcspring.rx.observeEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.apache.logging.log4j.kotlin.Logging
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object Gui : Logging {

    internal val currentGuis = mutableMapOf<Player, GuiBase>()
    internal val plugin = JavaPlugin.getProvidingPlugin(InventoryGui::class.java)

    fun getActiveGui(player: Player): GuiBase? {
        val parentGui = currentGuis[player]
        logger.debug { "Active GUI lookup for $player: $parentGui" }
        return parentGui
    }

    @Suppress("UNCHECKED_CAST")
    fun <
            Drawer : FunctionalGuiDrawer,
            Setup : FunctionalGuiSetup,
            GuiType : GuiBase
            > setup(
            gui: FunctionalGui<Drawer, Setup, GuiType>,
            lambda: GuiBuilder<Setup, Drawer>.() -> Unit
    ): Disposable {
        val builder = GuiBuilder<Setup, Drawer>()
        builder.lambda()
        builder.setup?.apply { gui.setupLambdas.add(this) }
        builder.redraw?.apply { gui.redrawLambdas.add(this) }
        return gui.open()
    }

    abstract class Chat(
            plugin: Plugin,
            player: Player,
            parent: GuiBase? = getActiveGui(player)
    ) : ChatGui(plugin, player, parent)

    abstract class Hotbar(
            plugin: Plugin,
            player: Player,
            parent: GuiBase? = getActiveGui(player)
    ) : HotbarGui(plugin, player, parent)

    abstract class Inventory(
            plugin: Plugin,
            player: Player,
            title: String,
            size: Int = InventorySize.ONE_LINE,
            parent: GuiBase? = getActiveGui(player)
    ) : InventoryGui(plugin, player, title, size, parent)
}

abstract class GuiBase(
        val plugin: Plugin,
        val player: Player,
        var parent: GuiBase? = null
) : Disposable, Logging {

    lateinit var globalSubscription: CompositeDisposable
    lateinit var listenerSubscription: CompositeDisposable

    protected open fun initialize() {}
    protected open fun setup() {}
    protected abstract fun registerListeners(): CompositeDisposable

    abstract fun redraw()
    abstract fun clear()

    open fun open(): Disposable {
        logger.debug { "Opening gui for ${player.name}" }
        require(!this::globalSubscription.isInitialized) { "GUI can only be opened once" }
        if (parent == null) {
            parent = Gui.currentGuis[player]
        }
        logger.debug {
            parent?.let { "Created new GUI with parent ${it::class.simpleName}" }
                    ?: "Created new GUI with no parent"
        }
        require(parent?.isDisabled()
                ?: true) { "Parent must be disabled/closed before a new GUI can be opened" }
        globalSubscription = makeDefaultSubscription()
        initialize()
        enable()
        return globalSubscription
    }

    fun isDisabled() = listenerSubscription.isDisposed

    override fun isDisposed() = globalSubscription.isDisposed

    override fun dispose() = close()

    open fun close() {
        logger.debug { "Closing gui for ${player.name}" }
        require(!globalSubscription.isDisposed) { "GUI already closed" }
        globalSubscription.dispose()
    }

    open fun enable() {
        logger.debug { "Enabling gui ${this::class.simpleName} for ${player.name}" }
        logger.debug { "Setting current GUI for $player" }
        Gui.currentGuis[player] = this
        require(this::globalSubscription.isInitialized) { "GUI enable called before open" }
        require(!this::listenerSubscription.isInitialized || listenerSubscription.isDisposed) {
            "Cannot enable, already enabled"
        }
        listenerSubscription = registerListeners()
        setup()
        redraw()
    }

    open fun disable() {
        logger.debug { "Disabling gui ${this::class.simpleName} for ${player.name}" }
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

        subscription.add(Disposable.fromRunnable {
            logger.debug { "Running dispose for ${this::class.simpleName}: ${player.name}" }
            if (!isDisabled()) {
                logger.debug { "GUI ${this::class.simpleName} not disabled, disabling..." }
                disable()
            }

            logger.debug { "Removing current GUI ${this::class.simpleName} for $player" }
            Gui.currentGuis.remove(player)

            val tempParent = parent
            if (tempParent != null) {
                logger.debug { "Parent GUI found, enabling ${tempParent::class.simpleName}..." }
                tempParent.enable()
            } else {
                logger.debug { "No parent GUI, skipping parent enable" }
            }
        })
        return subscription
    }
}
