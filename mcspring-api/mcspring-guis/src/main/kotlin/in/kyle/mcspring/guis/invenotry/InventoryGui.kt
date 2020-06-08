package `in`.kyle.mcspring.guis.invenotry

import `in`.kyle.mcspring.chat.translateColorCodes
import `in`.kyle.mcspring.guis.ClickContext
import `in`.kyle.mcspring.guis.ClickableGui
import `in`.kyle.mcspring.guis.item.ItemBuilder
import `in`.kyle.mcspring.rx.observeEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import jdk.jshell.JShell
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class InventorySetup(gui: InventoryGui) : InventoryDrawer(gui) {
    val listenerSubscription: CompositeDisposable = gui.listenerSubscription
    val globalSubscription: CompositeDisposable = gui.globalSubscription
}

open class InventoryDrawer(val inventory: InventoryGui) {
    val player = inventory.player
    val plugin = inventory.plugin
    val lastItemIndex: Int by lazy { inventory.bukkitInventory.size - 1 }

    fun button(x: Int, y: Int, lambda: InventoryItemBuilder.() -> Unit) {
        require(x < 9 && x > -1) { "x is out of range! $x" }
        button(x + y * 9, lambda)
    }

    fun button(slot: Int, lambda: InventoryItemBuilder.() -> Unit) {
        require(slot < inventory.bukkitInventory.size) { "Slot out of bounds $slot < ${inventory.bukkitInventory.size}" }

        val builder = InventoryItemBuilder()
        builder.lambda()

        builder.click?.let { inventory.actions[slot] = it }
        inventory.bukkitInventory.setItem(slot, builder.itemStack)
    }

    fun redraw() = inventory.redraw()
    fun close() = inventory.close()
    fun title(lambda: () -> String) = inventory.title(lambda)
    fun enable() = inventory.enable()
    fun disable(closeInventory: Boolean = true) = inventory.disable(closeInventory)
    fun closeInventoryOnDisable(boolean: Boolean) { inventory.closeInventoryOnDisable = boolean }
}

class InventoryGui constructor(
    val size: Int,
    var title: String,
    plugin: Plugin,
    player: Player,
    parent: ClickableGui<out Any, out Any>?
) : ClickableGui<InventoryDrawer, InventorySetup>(plugin, player, parent) {

    val actions = mutableMapOf<Int, ClickContext.() -> Unit>()

    internal lateinit var bukkitInventory: Inventory
    var closeInventoryOnDisable = true

    init {
        setupLambdas.add {
            if (player.openInventory.topInventory != bukkitInventory) {
                player.openInventory(bukkitInventory)
            }
        }
    }

    override fun close() {
        require(player.openInventory.topInventory == bukkitInventory) { "Inventory already closed" }
        super.close()
    }

    fun title(title: () -> String) =
            InventoryTitleHelper.sendInventoryTitle(
                    player,
                    title()
            )

    override fun open(): Disposable {
        if (parent is InventoryGui) {
            title { title.translateColorCodes() }
            bukkitInventory = parent.bukkitInventory
        } else {
            bukkitInventory = Bukkit.createInventory(player, size, title.translateColorCodes())
            player.openInventory(bukkitInventory)
            player.updateInventory()
        }
        return super.open()
    }

    fun disable(closeInventory: Boolean) {
        val temp = closeInventoryOnDisable
        closeInventoryOnDisable = closeInventory
        disable()
        closeInventoryOnDisable = temp
    }

    override fun disable() {
        super.disable()
        if (closeInventoryOnDisable) {
            Bukkit.getScheduler().runTask(plugin, Runnable { player.closeInventory() })
        }
    }

    override fun registerListeners(): CompositeDisposable {
        val listeners = CompositeDisposable()
        listeners.add(plugin.observeEvent(InventoryClickEvent::class)
            .filter { isCorrectPlayer(it) }
            .subscribe {
                if (it.click in listOf(LEFT, RIGHT, MIDDLE)) {
                    actions[it.rawSlot]?.invoke(ClickContext(this))
                }
                it.isCancelled = true
            })

        listeners.add(plugin.observeEvent(InventoryDragEvent::class)
            .filter { event -> event.inventory == bukkitInventory }
            .subscribe { it.isCancelled = true })
        return listeners
    }

    private fun isCorrectPlayer(event: InventoryEvent): Boolean {
        return event.inventory.holder != null
                && event.inventory.holder == player
                && this::bukkitInventory.isInitialized
                && event.inventory == bukkitInventory
    }

    override fun makeDrawer() = InventoryDrawer(this)

    override fun makeSetup() = InventorySetup(this)

    override fun clear() {
        bukkitInventory.clear()
        player.updateInventory()
    }

}

enum class InventorySize {
    ONE_LINE, TWO_LINE, THREE_LINE, FOUR_LINE, FIVE_LINE, SIX_LINE;

    val slots: Int = 9 * (ordinal + 1)

    companion object {
        fun of(requiredSlots: Int) = values().first { it.slots >= requiredSlots }
    }
}

class InventoryItemBuilder {

    internal var itemStack: ItemStack? = null
    internal var click: (ClickContext.() -> Unit)? = null

    fun itemStack(lambda: ItemBuilder.() -> Unit) {
        this.itemStack = ItemBuilder.create(lambda)
    }

    fun onClick(lambda: ClickContext.() -> Unit) {
        click = lambda
    }
}

