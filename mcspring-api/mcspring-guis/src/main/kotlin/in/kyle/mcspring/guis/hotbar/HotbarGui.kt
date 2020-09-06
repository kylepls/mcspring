package `in`.kyle.mcspring.guis.hotbar

import `in`.kyle.mcspring.guis.GuiBase
import `in`.kyle.mcspring.rx.observeEvent
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

abstract class HotbarGui(plugin: Plugin, player: Player, parent: GuiBase?) : GuiBase(plugin, player, parent) {

    private var lastClickTime = -1L

    private val clickEventHandler = { event: Cancellable ->
        val slot = player.inventory.heldItemSlot
        if (notSpamClick()) {
            onClick(slot)
            event.isCancelled = true
        }
    }

    abstract fun onClick(slot: Int): Boolean

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
                .filter { it.action !== Action.PHYSICAL }
                .subscribe(clickEventHandler))

        listeners.add(plugin.observeEvent(BlockPlaceEvent::class)
                .filter { it.player == player }
                .subscribe(clickEventHandler))

        listeners.add(plugin.observeEvent(BlockBreakEvent::class)
                .filter { it.player == player }
                .subscribe(clickEventHandler))

        return listeners
    }

    override fun clear() {
        for (i in 0..8) {
            player.inventory.setItem(i, null)
        }
        player.updateInventory()
    }
}
