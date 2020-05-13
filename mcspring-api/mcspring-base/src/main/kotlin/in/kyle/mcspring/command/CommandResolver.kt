package `in`.kyle.mcspring.command

import org.bukkit.command.CommandSender

interface CommandResolver {
    fun makeResolver(command: Command): ParameterResolver

    data class Command(val sender: CommandSender, val args: List<String>, val label: String)
}
