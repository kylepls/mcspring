package `in`.kyle.mcspring

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.bukkit.plugin.Plugin
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.DefaultResourceLoader
import java.util.*

class SpringPlugin(
        private val plugin: Plugin
) {

    private var context: ConfigurableApplicationContext? = null

    fun onDisable(plugin: Plugin) {
        if (context != null) {
            context!!.close()
            context = null
            SETUP_PLUGINS.remove(plugin)
        }
    }

    private fun initSpring(config: Class<*>) {
        val builder = SpringApplicationBuilder()
        var sources = arrayOf(config, SpringSpigotSupport::class.java)

        if (SETUP_PLUGINS.isNotEmpty()) {
            val parent = SETUP_PLUGINS.entries.reduce { _, b -> b }.value
            builder.parent(parent.context)
            sources = sources.copyOfRange(0, 1)
        }

        context = builder.sources(*sources)
                .resourceLoader(DefaultResourceLoader(plugin.javaClass.classLoader))
                .bannerMode(Banner.Mode.OFF)
                .properties("spigot.plugin=" + plugin.name)
                .logStartupInfo(true)
                .run()
    }

    companion object {
        private val SETUP_PLUGINS = LinkedHashMap<Plugin, SpringPlugin>()

        @JvmStatic
        fun setup(plugin: Plugin, config: Class<*>) {
            setupLogger()
            val springPlugin = SpringPlugin(plugin)
            springPlugin.initSpring(config)
            SETUP_PLUGINS[plugin] = springPlugin
        }

        @JvmStatic
        fun teardown(plugin: Plugin) {
            SETUP_PLUGINS.remove(plugin)?.onDisable(plugin)
        }

        private fun setupLogger() {
            if (SETUP_PLUGINS.isEmpty()) {
                val rootLogger = LogManager.getRootLogger() as Logger
                rootLogger.level = Level.ALL
            }
        }
    }
}
