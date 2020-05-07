package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.manager.controller.PluginController
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommand
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
@ConditionalOnBean(Plugin::class)
internal class CommandPlugin(
    private val pluginController: PluginController
) {

    @Command(value = "plugin", aliases = ["pl"], description = "Load/unload/reload a specific plugin", usage = "/plugin <load|unload|list>")
    fun plugin(command: PluginCommand) {
        command.on("load", ::load)
        command.on("unload", ::unload)
        command.on("list", ::executeListPlugins)
        command.otherwise("Usage: /plugin <load|unload|list>")
    }

    private fun load(command: PluginCommand) {
        command.withMap<Path>(pluginController.loadablePlugins) { "Plugin $it not found or is already loaded" }
        command.then(::executeLoad)
        command.otherwise("Usage: /plugin load <name>")
    }

    private fun unload(command: PluginCommand) {
        val plugins = pluginController.plugins.associateBy({ it.name }, { it })
        command.withMap(plugins) { "Plugin $it is not loaded" }
        command.then(::executeDisable)
        command.otherwise("Usage: /plugin unload <name>")
    }

    private fun executeListPlugins(): String {
        val colors = mapOf(true to "&1", false to "&2")
        return pluginController.allPlugins
                .map { "${colors[it.value]}${it.key}" }
                .joinToString(separator = " ")
    }

    private fun executeLoad(jar: Path): String {
        val plugin = pluginController.load(jar)

        return if (plugin != null) {
            "Plugin ${plugin.name} enabled"
        } else {
            "&4Could not load $jar see log for details"
        }
    }

    private fun executeDisable(plugin: Plugin): String {
        val disabled = pluginController.unload(plugin)
        return if (disabled) {
            "Plugin $plugin disabled"
        } else {
            "Could not disable $plugin, see log for details"
        }
    }
}
