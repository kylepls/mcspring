package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.commands.dsl.command
import `in`.kyle.mcspring.manager.controller.PluginController
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import `in`.kyle.mcspring.commands.dsl.mcspring.Command
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
@ConditionalOnBean(Plugin::class)
internal class CommandPlugin(private val pluginController: PluginController) {

    @Command(
            value = "plugin",
            aliases = ["pl"],
            description = "Load/unload/reload a specific plugin",
            usage = "/plugin <load|unload|list>"
    )
    fun plugin() = command {
        subcommand {
            on("load", commandExecutor = load())
            on("unload", commandExecutor = unload())
            on("list") { then { message(executeListPlugins()) } }

            missing {
                val subs = subCommands.keys.joinToString(separator = "|") { it.first() }
                message("Usage: $label <$subs>")
            }
        }
    }

    private fun load() = command {
        val path = mapArg<Path> {
            parser {
                map(pluginController.loadablePlugins)
            }
            invalid { message("Plugin $it not found or it is already loaded") }
        }
        then { executeLoad(path) }
    }

    private fun unload() = command {
        val plugin = mapArg<Plugin> {
            parser {
                map(pluginController.plugins.associateBy({ it.name }, { it }))
            }
            invalid { message("Plugin $it is not loaded") }
            missing { message("Usage: $label ${args[0]} <name>") }
        }
        then { message(executeDisable(plugin)) }
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
