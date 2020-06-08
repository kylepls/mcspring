package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.common.commands.CommandMapWrapper
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

object CommandUtils {

    fun register(plugin: Plugin, meta: CommandMeta) {
        meta.preRegister()
        CommandMapWrapper.registerCommand(plugin, object : Command(meta.name, meta.description, meta.usageMessage, meta.aliases) {
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
        })
    }
}
