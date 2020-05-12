package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandImpl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.mockito.Mockito.mock

object TestConsole {

    fun runCommand(commandString: String,
                   consumer: (PluginCommand) -> Unit,
                   runExecutors: Boolean = true,
                   sender: CommandSender = mock(Player::class.java)): PluginCommandImpl {
        val command = makeCommand(commandString, sender, runExecutors)
        consumer(command)
        return command
    }

    fun makeCommand(command: String,
                            player: CommandSender = mock(Player::class.java),
                            runExecutors: Boolean = true):
            PluginCommandImpl {
        return PluginCommandImpl(SimpleMethodInjection(listOf()),
                player,
                command.split(" ").filter { it.isNotBlank() }.toMutableList(),
                runExecutors)
    }
}
