package `in`.kyle.mcspring.commands.dsl.util

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandMap
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory

object CommandMapWrapper {

    val logger by lazy { LoggerFactory.getLogger(CommandMapWrapper::class.java) }

    val commandMap by lazy {
        val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        bukkitCommandMap.isAccessible = true
        bukkitCommandMap[Bukkit.getServer()] as CommandMap
    }

    fun registerCommand(plugin: Plugin, command: Command) {
        require(plugin is JavaPlugin) { "Plugin must be an instance of JavaPlugin to register commands" }
        val pluginCommand = plugin.getCommand(command.name)
        if (pluginCommand != null) {
            pluginCommand.apply {
                val (executor, tabCompleter) = delegateTabsAndCommands(command)
                setExecutor(executor)
                setTabCompleter(tabCompleter)
            }
        } else {
            val registered = commandMap.register(command.label, plugin.name, command)
            if (!registered) {
                logger.warn("""Could not register ${command.label} for ${plugin.name}. 
                           There is a command label conflict.
                           Using aliases instead.""".trimIndent())
            }
        }
    }

    private fun delegateTabsAndCommands(command: Command): Pair<CommandExecutor, TabCompleter> {
        return Pair(
                CommandExecutor { sender, _, label, args -> command.execute(sender, label, args) },
                TabCompleter { sender, _, alias, args -> command.tabComplete(sender, alias, args) }
        )
    }
}
