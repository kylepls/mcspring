package `in`.kyle.mcspring.guis.inventory

import `in`.kyle.mcspring.guis.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class FunctionalInventoryGui(
        player: Player,
        title: String,
        size: Int,
        plugin: Plugin,
        parent: GuiBase?
) : FunctionalGui<InventoryGuiDrawer, InventoryGuiSetup, InventoryGui>(player, plugin) {

    internal val actions = mutableMapOf<Int, () -> Unit>()

    override val gui = object : InventoryGui(plugin, player, title, size, parent) {
        override fun onClick(slot: Int) = actions[slot]?.invoke() ?: Unit
        override fun setup() = runSetupLambdas()
        override fun redraw() = runRedrawLambdas()
    }

    override fun makeDrawer() = InventoryGuiDrawer(this)
    override fun makeSetup() = InventoryGuiSetup(this)
}

class InventoryGuiSetup(
        inventory: FunctionalInventoryGui
) : InventoryGuiDrawer(inventory), FunctionalGuiSetup {
    override val listenerSubscription: CompositeDisposable = inventory.gui.listenerSubscription
    override val globalSubscription: CompositeDisposable = inventory.gui.globalSubscription
}

open class InventoryGuiDrawer(val inventory: FunctionalInventoryGui) : FunctionalGuiDrawer {
    override val plugin = inventory.gui.plugin
    override val player = inventory.player

    val lastItemIndex: Int
        get() = inventory.gui.lastItemIndex

    fun button(x: Int, y: Int, lambda: InventoryItemBuilder.() -> Unit) = inventory.gui.button(x, y, lambda)

    fun button(slot: Int, lambda: InventoryItemBuilder.() -> Unit) = inventory.gui.button(slot, lambda)

    fun title(lambda: () -> String) {
        inventory.gui.title = lambda()
    }

    fun redraw() = inventory.gui.redraw()
    fun close() = inventory.gui.close()
    fun enable() = inventory.gui.enable()
    fun disable(closeInventory: Boolean = true) = inventory.gui.disable(closeInventory)
    fun closeInventoryOnDisable(boolean: Boolean) {
        inventory.gui.closeInventoryOnDisable = boolean
    }
}

fun Gui.inventory(
        player: Player,
        title: String,
        size: Int = InventorySize.ONE_LINE,
        lambda: GuiBuilder<InventoryGuiSetup, InventoryGuiDrawer>.() -> Unit
): Disposable {
    val functionalInventoryGui = FunctionalInventoryGui(player, title, size, plugin, getActiveGui(player))
    return setup(functionalInventoryGui, lambda)
}
