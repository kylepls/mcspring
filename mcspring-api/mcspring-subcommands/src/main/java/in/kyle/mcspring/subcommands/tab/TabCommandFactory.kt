package `in`.kyle.mcspring.subcommands.tab

import `in`.kyle.mcspring.command.CommandResolver
import `in`.kyle.mcspring.command.SimpleCommandFactory
import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import org.bukkit.command.Command as BukkitCommand
import org.bukkit.command.PluginCommand as BukkitPluginCommand

@Primary
@Component
@ConditionalOnBean(Plugin::class)
internal class TabCommandFactory(
        injection: SimpleMethodInjection,
        commandResolvers: Set<CommandResolver>,
        val plugin: Plugin,
        private val tabDiscovery: TabDiscovery
) : SimpleCommandFactory(injection, commandResolvers, plugin) {

    override fun makeCommand(method: Method, obj: Any, name: String): BukkitCommand {
        val command = super.makeCommand(method, obj, name) as BukkitPluginCommand
        if (method.parameterCount == 1
                && method.parameters[0].type.isAssignableFrom(PluginCommand::class.java)) {
            command.tabCompleter = makeTabCompleter(method, obj)
        }
        return command
    }

    private fun methodToConsumer(method: Method, obj: Any): (PluginCommand) -> Unit {
        return {
            method.isAccessible = true
            method.invoke(obj, it)
        }
    }

    private fun makeTabCompleter(method: Method, obj: Any): TabCompleter {
        return TabCompleter { sender: CommandSender, _: BukkitCommand, _: String, strings: Array<String> ->
            val consumer = methodToConsumer(method, obj)
            tabDiscovery.getCompletions(sender, strings.joinToString(" "), consumer)
        }
    }
}
