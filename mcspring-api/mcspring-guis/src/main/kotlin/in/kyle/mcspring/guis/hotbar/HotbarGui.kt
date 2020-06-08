package `in`.kyle.mcspring.guis.hotbar

import `in`.kyle.mcspring.guis.ClickContext
import `in`.kyle.mcspring.guis.ClickableGui
import `in`.kyle.mcspring.guis.item.ItemBuilder
import `in`.kyle.mcspring.rx.observeEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.Action.PHYSICAL
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import kotlin.collections.set

open class HotbarDrawer(val hotbar: HotbarGui) {

    val player = hotbar.player

    fun button(slot: Int, lambda: HotbarButtonBuilder.() -> Unit) {
        val builder = HotbarButtonBuilder()
        builder.lambda()
        if (builder.click != null) {
            hotbar.actions[slot] = builder.click!!
        }
        player.inventory.setItem(slot, builder.item)
    }

    fun redraw() = hotbar.redraw()

    fun close() = hotbar.close()

    fun enable() = hotbar.enable()

    fun disable() = hotbar.disable()
}

class HotbarSetup(hotbar: HotbarGui) : HotbarDrawer(hotbar) {
    val listenerSubscription = hotbar.listenerSubscription
    val globalSubscription = hotbar.globalSubscription
}

class HotbarGui(
    plugin: Plugin, player: Player, parent: ClickableGui<out Any, out Any>?
) : ClickableGui<HotbarDrawer, HotbarSetup>(plugin, player, parent) {

    val actions = mutableMapOf<Int, ClickContext.() -> Unit>()
    private var lastClickTime = -1L

    override fun clear() {
        actions.clear()
        for (i in 0..8) {
            player.inventory.setItem(i, null)
        }
        player.updateInventory()
    }

    override fun redraw() {
        super.redraw()
        player.updateInventory()
    }

    private val clickEventHandler = { event: Cancellable ->
        val slot = player.inventory.heldItemSlot
        if (slot in actions) {
            event.isCancelled = true
            if (notSpamClick()) {
                actions[slot]?.invoke(ClickContext(this))
            }
        }
    }

    override fun registerListeners(): CompositeDisposable {
        val listeners = CompositeDisposable()
        listeners.add(plugin.observeEvent(PlayerDropItemEvent::class)
            .filter { it.player == player }
            .subscribe(clickEventHandler))

        listeners.add(plugin.observeEvent(InventoryCreativeEvent::class, InventoryClickEvent::class)
            .filter { player == it.whoClicked }
            .filter { it.clickedInventory?.type == InventoryType.PLAYER }
            .subscribe {
                it.isCancelled = true
                player.setItemOnCursor(ItemStack(Material.AIR))
                player.updateInventory()
            })

        listeners.add(plugin.observeEvent(PlayerInteractEvent::class)
            .filter { player == it.player }
            .filter { it.action !== PHYSICAL }
            .subscribe(clickEventHandler))

        listeners.add(plugin.observeEvent(BlockPlaceEvent::class)
            .filter { it.player == player }
            .subscribe(clickEventHandler))

        listeners.add(plugin.observeEvent(BlockBreakEvent::class)
            .filter { it.player == player }
            .subscribe(clickEventHandler))

        return listeners
    }

    override fun makeDrawer() = HotbarDrawer(this)

    override fun makeSetup() = HotbarSetup(this)

    private fun notSpamClick(): Boolean {
        return if (lastClickTime == -1L) {
            lastClickTime = System.currentTimeMillis()
            true
        } else {
            val duration = System.currentTimeMillis() - lastClickTime
            if (duration > 100) {
                lastClickTime = System.currentTimeMillis()
                true
            } else {
                false
            }
        }
    }
}

class HotbarButtonBuilder {

    internal lateinit var item: ItemStack
    internal var click: (ClickContext.() -> Unit)? = null

    fun onClick(click: ClickContext.() -> Unit) {
        this.click = click
    }

    fun itemStack(lambda: ItemBuilder.() -> Unit) {
        this.item = ItemBuilder.create(lambda)
    }
}

