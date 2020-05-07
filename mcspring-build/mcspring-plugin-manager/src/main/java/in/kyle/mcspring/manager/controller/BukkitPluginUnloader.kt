package `in`.kyle.mcspring.manager.controller

import lombok.SneakyThrows
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.net.URLClassLoader
import javax.annotation.PostConstruct

@Lazy
@Component
@ConditionalOnBean(Plugin::class)
class BukkitPluginUnloader(
        private val pluginManager: PluginManager,
        private val commandMap: CommandMap
) {

    private val commands = mutableMapOf<String, Command>()
    private val plugins = mutableListOf<Plugin>()
    private val names = mutableMapOf<String, Plugin>()

    @PostConstruct
    fun setup() {
        plugins.addAll(getDeclaredField(pluginManager, "plugins"))
        names.putAll(getDeclaredField(pluginManager, "lookupNames"))
        val knownCommands = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
        knownCommands.isAccessible = true
        commands.putAll(knownCommands.get(commandMap) as Map<String, Command>)
    }

    fun unload(plugin: Plugin): Boolean {
        pluginManager.disablePlugin(plugin)
        synchronized(pluginManager) {
            plugins.remove(plugin)
            names.remove(plugin.name)
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
        unregister.forEach { it.value.unregister(commandMap); commands.remove(it.key) }
    }

    @SneakyThrows
    private fun closeClassLoader(classLoader: ClassLoader) {
        if (classLoader is URLClassLoader) {
            setDeclaredField(classLoader, "plugin", null)
            setDeclaredField(classLoader, "pluginInit", null)
            classLoader.close()
        }
    }

    @SneakyThrows
    private fun <T> getDeclaredField(obj: Any, fieldName: String): T {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        return field[obj] as T
    }

    @SneakyThrows
    private fun setDeclaredField(obj: Any, fieldName: String, value: Any?) {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field[obj] = value
    }
}
