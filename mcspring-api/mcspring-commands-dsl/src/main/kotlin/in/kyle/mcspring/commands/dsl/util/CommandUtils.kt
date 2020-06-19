package `in`.kyle.mcspring.commands.dsl.util

import `in`.kyle.mcspring.commands.dsl.CommandContext
import `in`.kyle.mcspring.commands.dsl.CommandMeta
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

object CommandUtils {

    fun register(plugin: Plugin, meta: CommandMeta) {
        meta.preRegister()
        CommandMapWrapper.registerCommand(plugin, makeBukkitCommand(meta).apply {
            aliases = meta.aliases
            description = meta.description
            usage = meta.usageMessage
            permission = meta.usageMessage.takeIf { it.isNotBlank() }
            permissionMessage = meta.permissionMessage.takeIf { it.isNotBlank() }
        })
    }

    private fun makeBukkitCommand(meta: CommandMeta) = object : Command(meta.name) {
        override fun execute(
                sender: CommandSender,
                commandLabel: String,
                args: Array<out String>
        ): Boolean {
            val provider = meta.executor.provider
            val context = CommandContext(sender, label, args.toList())
            provider(context)
            return true
        }
    }
}
