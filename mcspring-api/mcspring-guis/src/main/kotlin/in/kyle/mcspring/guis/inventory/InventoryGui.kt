package `in`.kyle.mcspring.guis.inventory

import `in`.kyle.mcspring.chat.translateColorCodes
import `in`.kyle.mcspring.guis.GuiBase
import `in`.kyle.mcspring.guis.item.ItemBuilder
import `in`.kyle.mcspring.rx.observeEvent
import `in`.kyle.mcspring.rx.syncScheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

abstract class InventoryGui(
        plugin: Plugin,
        player: Player,
        title: String,
        val size: Int,
        parent: GuiBase? = null
) : GuiBase(plugin, player, parent) {

    private val validClicks = listOf(ClickType.LEFT, ClickType.RIGHT, ClickType.MIDDLE)

    private val actions = mutableMapOf<Int, () -> Unit>()

    lateinit var bukkitInventory: Inventory
    var closeInventoryOnDisable = true
    var title = title
        set(value) {
            updateViewingTitle(value)
            field = value
        }

    val lastItemIndex: Int by lazy { bukkitInventory.size - 1 }

    open fun onClick(slot: Int) {}

    override fun close() {
        require(player.openInventory.topInventory == bukkitInventory) { "Inventory already closed" }
        super.close()
    }

    override fun open(): Disposable {
        val tempParent = parent
        if (tempParent is InventoryGui) {
            updateViewingTitle(title)
            bukkitInventory = tempParent.bukkitInventory
        } else {
            bukkitInventory = Bukkit.createInventory(player, size, title.translateColorCodes())
            player.openInventory(bukkitInventory)
            player.updateInventory()
        }
        return super.open()
    }

    override fun disable() {
        super.disable()
        if (closeInventoryOnDisable) {
            Bukkit.getScheduler().runTask(plugin, Runnable { player.closeInventory() })
        }
    }

    fun disable(closeInventory: Boolean) {
        val temp = closeInventoryOnDisable
        closeInventoryOnDisable = closeInventory
        disable()
        closeInventoryOnDisable = temp
    }

    override fun enable() {
        if (player.openInventory.topInventory != bukkitInventory) {
            player.openInventory(bukkitInventory)
        }
        super.enable()
    }

    override fun clear() {
        bukkitInventory.clear()
        player.updateInventory()
    }

    override fun registerListeners(): CompositeDisposable {
        val listeners = CompositeDisposable()
        listeners.add(plugin.observeEvent(InventoryClickEvent::class)
                .filter { isCorrectPlayer(it) }
                .subscribe {
                    if (it.click in validClicks) {
                        val slot = it.rawSlot
                        actions[slot]?.invoke()
                        onClick(slot)
                    }
                    it.isCancelled = true
                })

        listeners.add(plugin.observeEvent(InventoryDragEvent::class)
                .filter { it.inventory == bukkitInventory }
                .subscribe { it.isCancelled = true })

        listeners.add(plugin.observeEvent(InventoryCloseEvent::class)
                .filter { it.player == player }
                .subscribe {
                    listeners.add(plugin.syncScheduler().scheduleDirect({
                        it.player.openInventory(bukkitInventory)
                    }, 100, TimeUnit.MILLISECONDS))
                })
        return listeners
    }

    fun button(x: Int, y: Int, lambda: InventoryItemBuilder.() -> Unit) {
        require(x < 9 && x > -1) { "x is out of range! $x" }
        button(x + y * 9, lambda)
    }

    fun button(slot: Int, lambda: InventoryItemBuilder.() -> Unit) {
        require(slot < bukkitInventory.size) {
            "Slot out of bounds $slot < ${bukkitInventory.size}"
        }

        val builder = InventoryItemBuilder()
        builder.lambda()

        builder.click?.let { actions[slot] = it }
        bukkitInventory.setItem(slot, builder.itemStack)
    }

    private fun isCorrectPlayer(event: InventoryEvent): Boolean {
        return event.inventory.holder != null
                && event.inventory.holder == player
                && this::bukkitInventory.isInitialized
                && event.inventory == bukkitInventory
    }

    private fun updateViewingTitle(title: String) {
        InventoryTitleHelper.sendInventoryTitle(player, title.translateColorCodes())
    }
}

class InventoryItemBuilder {

    internal var itemStack: ItemStack? = null
    internal var click: (() -> Unit)? = null

    fun itemStack(lambda: ItemBuilder.() -> Unit) {
        this.itemStack = ItemBuilder.create(lambda)
    }

    fun itemStack(itemStack: ItemStack) {
        this.itemStack = itemStack
    }

    fun onClick(lambda: () -> Unit) {
        click = lambda
    }
}

@Suppress("MemberVisibilityCanBePrivate")
object InventorySize {
    const val ONE_LINE = 9 * 1
    const val TWO_LINE = 9 * 2
    const val THREE_LINE = 9 * 3
    const val FOUR_LINE = 9 * 4
    const val FIVE_LINE = 9 * 5
    const val SIX_LINE = 9 * 6

    fun of(requiredSlots: Int) = arrayOf(
            ONE_LINE,
            TWO_LINE,
            THREE_LINE,
            FOUR_LINE,
            FIVE_LINE,
            SIX_LINE
    ).first { it >= requiredSlots }
}
