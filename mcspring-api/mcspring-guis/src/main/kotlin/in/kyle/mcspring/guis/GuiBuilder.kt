package `in`.kyle.mcspring.guis

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
