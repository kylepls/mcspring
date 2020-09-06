package `in`.kyle.mcspring.manager.controller

import org.bukkit.plugin.InvalidDescriptionException
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.PluginManager
import org.springframework.stereotype.Controller
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

@Controller
class PluginController(
        private val pluginManager: PluginManager,
        private val pluginLoader: PluginLoader,
        private val unloader: BukkitPluginUnloader,
        private val logger: Logger
) {

    val pluginsFolder = Paths.get("plugins")

    val loadablePlugins: Map<String, Path>
        get() {
            return Files.list(pluginsFolder)
                    .filter { it.toString().endsWith(".jar") }
                    .filter { getPluginName(it) != null }
                    .collect(Collectors.toMap({ getPluginName(it) }, { it }))
        }

    val plugins: Set<Plugin>
        get() = pluginManager.plugins.toSet()

    val allPlugins: Map<String, Boolean>
        get() {
            val allPlugins = mutableMapOf<String, Boolean>()
            loadablePlugins.forEach { allPlugins[it.key] = false }
            plugins.forEach { allPlugins[it.name] = it.isEnabled }
            return allPlugins
        }

    fun load(jar: Path): Plugin? {
        return try {
            val plugin: Plugin? = pluginManager.loadPlugin(jar.toFile())
            plugin?.onLoad()
            plugin
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Could not load " + jar.toAbsolutePath())
            logger.log(Level.SEVERE, e) { "" }
            null
        }
    }

    fun unload(plugin: Plugin): Boolean {
        if (pluginManager.isPluginEnabled(plugin)) {
            try {
                return unloader.unload(plugin)
            } catch (e: Exception) {
                logger.log(Level.SEVERE, "Could not unload " + plugin.name)
                logger.log(Level.SEVERE, e) { "" }
            }
        }
        return false
    }

    fun isPluginJar(jar: Path): Boolean {
        return try {
            pluginLoader.getPluginDescription(jar.toFile())
            true
        } catch (_: InvalidDescriptionException) {
            false
        }
    }

    fun reload(jar: Path) {
        val description = pluginLoader.getPluginDescription(jar.toFile())
        val name = description.name
        val plugin = getPlugin(name)
        requireNotNull(plugin) {"Plugin $name is not loaded and therefore cannot be reloaded."}
        unload(plugin)
        load(jar)
    }

    fun getPlugin(name: String): Plugin? {
        return pluginManager.getPlugin(name)
    }

    fun isEnabled(pluginName: String): Boolean {
        return getPlugin(pluginName)?.isEnabled ?: false
    }

    private fun getPluginName(jar: Path): String? {
        val description = try {
            pluginLoader.getPluginDescription(jar.toFile())
        } catch (e: InvalidDescriptionException) {
            null
        }

        return description?.name
    }
}
