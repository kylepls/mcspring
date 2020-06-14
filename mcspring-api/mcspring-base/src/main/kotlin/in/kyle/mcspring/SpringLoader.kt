package `in`.kyle.mcspring

import io.github.classgraph.ClassGraph
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.loader.JarLauncher
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.io.DefaultResourceLoader
import java.net.URL
import java.net.URLClassLoader

class SpringLoader(
        private val javaPlugin: JavaPlugin,
        private val classLoader: ClassLoader
) {

    private var context: ConfigurableApplicationContext? = null
    private var logger = javaPlugin.logger

    fun onEnable() {
        try {
            initSpring()
        } catch (exception: Exception) {
            logger.warning("MCSpring Failed to load ${javaPlugin.name}")
            throw exception
        }
    }

    private fun initSpring() {
        val mains = ClassGraph()
                .enableAnnotationInfo()
                .scan(4)
                .allStandardClasses
                .filter { it.hasAnnotation("in.kyle.mcspring.annotation.SpringPlugin") }
                .map { it.name }
        require(mains.size == 1) { "There should only be 1 main class on the classpath: $mains" }
        logger.info("Using main class: $mains")
        val config = Class.forName(mains[0])
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
