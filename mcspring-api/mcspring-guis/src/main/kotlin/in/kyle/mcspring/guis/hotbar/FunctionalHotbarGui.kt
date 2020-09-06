package `in`.kyle.mcspring.guis.hotbar

import `in`.kyle.mcspring.guis.GuiBase
import `in`.kyle.mcspring.guis.FunctionalGui
import `in`.kyle.mcspring.guis.FunctionalGuiDrawer
import `in`.kyle.mcspring.guis.FunctionalGuiSetup
import `in`.kyle.mcspring.guis.item.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class FunctionalHotbarGui(
        plugin: Plugin,
        player: Player,
        parent: GuiBase?
) : FunctionalGui<HotbarGuiDrawer, HotbarGuiSetup, HotbarGui>(player, plugin) {

    internal val actions = mutableMapOf<Int, () -> Unit>()

    override val gui = object : HotbarGui(plugin, player, parent) {
        override fun onClick(slot: Int): Boolean {
            val action = actions[slot]
            action?.invoke()
            return action != null
        }

        override fun setup() = runSetupLambdas()
        override fun redraw() = runRedrawLambdas()
    }

    override fun makeDrawer() = HotbarGuiDrawer(this)
    override fun makeSetup() = HotbarGuiSetup(this)
}

class HotbarButtonBuilder {

    internal lateinit var item: ItemStack
    internal var click: (() -> Unit)? = null

    fun onClick(click: () -> Unit) {
        this.click = click
    }

    fun itemStack(lambda: ItemBuilder.() -> Unit) {
        this.item = ItemBuilder.create(lambda)
    }
}


open class HotbarGuiDrawer(private val hotbar: FunctionalHotbarGui) : FunctionalGuiDrawer {

    override val plugin = hotbar.plugin
    override val player = hotbar.player

    fun button(slot: Int, lambda: HotbarButtonBuilder.() -> Unit) {
        val builder = HotbarButtonBuilder().apply(lambda)
        val click = builder.click
        if (click != null) {
            hotbar.actions[slot] = click
        }
        player.inventory.setItem(slot, builder.item)
    }

    fun redraw() = hotbar.gui.redraw()
    fun close() = hotbar.gui.close()
    fun clear() = hotbar.gui.clear()
    fun enable() = hotbar.gui.enable()
    fun disable() = hotbar.gui.disable()
}

class HotbarGuiSetup(hotbar: FunctionalHotbarGui) : HotbarGuiDrawer(hotbar), FunctionalGuiSetup {
    override val listenerSubscription = hotbar.gui.listenerSubscription
    override val globalSubscription = hotbar.gui.globalSubscription
}
