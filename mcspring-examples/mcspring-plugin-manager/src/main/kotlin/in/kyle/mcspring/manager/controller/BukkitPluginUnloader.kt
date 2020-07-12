package `in`.kyle.mcspring.manager.controller

import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.event.Event
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredListener
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.net.URLClassLoader
import java.util.*
import javax.annotation.PostConstruct

@Lazy
@Component
class BukkitPluginUnloader(
        private val pluginManager: PluginManager,
        private val commandMap: CommandMap
) {

    private lateinit var commands: MutableMap<String, Command>
    private lateinit var plugins: MutableList<Plugin>
    private lateinit var names: MutableMap<String, Plugin>
    private lateinit var listeners: MutableMap<Event, SortedSet<RegisteredListener>>

    @PostConstruct
    fun setup() {
        plugins = getDeclaredField(pluginManager, "plugins")
        names = getDeclaredField(pluginManager, "lookupNames")
        listeners = getDeclaredField(pluginManager, "listeners")
        val knownCommands = SimpleCommandMap::class.java.getDeclaredField("knownCommands").apply {
            isAccessible = true
        }
        @Suppress("UNCHECKED_CAST")
        commands = knownCommands.get(commandMap) as MutableMap<String, Command>
    }

    fun unload(plugin: Plugin): Boolean {
        pluginManager.disablePlugin(plugin)
        synchronized(pluginManager) {
            plugins.remove(plugin)
            names.remove(plugin.name)
            unregisterListeners(plugin)
            unregisterCommands(plugin)
            closeClassLoader(plugin.javaClass.classLoader)
        }
        System.gc()
        return true
    }

    private fun unregisterCommands(plugin: Plugin) {
        val unregister = commands.entries
                .filter { (it.value as? PluginCommand)?.plugin === plugin }
                .toSet()
        unregister.forEach { it.value.unregister(commandMap) }
        unregister.forEach { commands.remove(it.key) }
    }

    private fun unregisterListeners(plugin: Plugin) {
        listeners.entries.removeIf { entry -> entry.value.all { it.plugin == plugin } }
    }

    private fun closeClassLoader(classLoader: ClassLoader) {
        if (classLoader is URLClassLoader) {
            setDeclaredField(classLoader, "plugin", null)
            setDeclaredField(classLoader, "pluginInit", null)
            classLoader.close()
        }
    }

    private fun <T> getDeclaredField(obj: Any, fieldName: String): T {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field[obj] as T
    }

    private fun setDeclaredField(obj: Any, fieldName: String, value: Any?) {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field[obj] = value
    }
}
