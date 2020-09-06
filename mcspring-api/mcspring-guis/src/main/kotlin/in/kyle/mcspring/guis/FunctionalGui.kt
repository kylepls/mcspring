package `in`.kyle.mcspring.guis

import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

abstract class FunctionalGui<Drawer : FunctionalGuiDrawer, Setup : FunctionalGuiSetup, GuiType : GuiBase>(
        val player: Player,
        val plugin: Plugin
) {

    val redrawLambdas = mutableSetOf<Drawer.() -> Unit>()
    val setupLambdas = mutableSetOf<Setup.() -> Unit>()

    abstract val gui: GuiType

    protected abstract fun makeDrawer(): Drawer
    protected abstract fun makeSetup(): Setup

    fun runSetupLambdas() {
        val setup = makeSetup()
        setupLambdas.forEach { setup.it() }
    }

    fun runRedrawLambdas() {
        val drawer = makeDrawer()
        redrawLambdas.forEach { drawer.it() }
    }

    fun open() = gui.open()
}

interface FunctionalGuiSetup : FunctionalGuiDrawer {
    val listenerSubscription: CompositeDisposable
    val globalSubscription: CompositeDisposable
}

interface FunctionalGuiDrawer {
    val plugin: Plugin
    val player: Player
}
