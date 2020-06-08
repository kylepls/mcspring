package `in`.kyle.mcspring.common.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.Plugin

object CommandMapWrapper {
    val commandMap by lazy {
        val bukkitCommandMap = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        bukkitCommandMap.isAccessible = true
        bukkitCommandMap[Bukkit.getServer()] as CommandMap
    }

    fun registerCommand(plugin: Plugin, command: Command) {
        commandMap.register(command.label, plugin.name, command)
    }
}
