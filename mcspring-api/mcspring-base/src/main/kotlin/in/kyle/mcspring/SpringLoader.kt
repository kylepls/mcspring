package `in`.kyle.mcspring

import org.bukkit.plugin.java.JavaPlugin
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.loader.JarLauncher
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.DefaultResourceLoader
import org.yaml.snakeyaml.Yaml
import java.net.URL
import java.net.URLClassLoader

class SpringLoader(
        private val javaPlugin: JavaPlugin,
        private val classLoader: ClassLoader
) {

    private var context: ConfigurableApplicationContext? = null
    private val logger by lazy { javaPlugin.logger }

    private val scanThreads by lazy {
        Runtime.getRuntime().availableProcessors()
    }

    fun onEnable() {
        try {
            initSpring()
        } catch (exception: Exception) {
            logger.warning("MCSpring Failed to load ${javaPlugin.name}")
            throw exception
        }
    }

    private fun initSpring() {
        val pluginYmlResource = javaPlugin.getResource("plugin.yml") ?: error("plugin.yml not found???")
        @Suppress("UNCHECKED_CAST")
        val yaml = Yaml().loadAs(pluginYmlResource, Map::class.java) as Map<String, Any>
        val main = yaml["spring-boot-main"] as? String
                ?: error("Spring boot main not found in plugin.yml")

        val config = Class.forName(main)
        val builder = SpringApplicationBuilder()
        var sources = arrayOf(config, SpringSpigotSupport::class.java)

        // TODO search
//        if (SETUP_PLUGINS.isNotEmpty()) {
//            val parent = SETUP_PLUGINS.entries.reduce { _, b -> b }.value
//            builder.parent(parent.context)
//            sources = sources.copyOfRange(0, 1)
//        }

        // this is sad :(
        val cl = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = classLoader

        context = builder.sources(*sources)
                .resourceLoader(DefaultResourceLoader(classLoader))
                .bannerMode(Banner.Mode.OFF)
                .properties("spigot.plugin=${javaPlugin.name}")
                .logStartupInfo(true)
                .run()
        Thread.currentThread().contextClassLoader = cl
    }

    fun onDisable() {
        if (context != null) {
            context!!.close()
            context = null
        }
    }

    class McSpringLoader : JarLauncher() {
        fun launch(classLoader: ClassLoader?) {
            val activeArchives = classPathArchivesIterator
            val addURL = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            addURL.isAccessible = true
            activeArchives.forEach { addURL(classLoader, it.url) }
        }

        override fun getMainClass() = ""
    }
}
