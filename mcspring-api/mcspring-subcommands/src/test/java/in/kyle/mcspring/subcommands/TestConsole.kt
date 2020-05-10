package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandImpl
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.mockito.Mockito.mock
import java.util.function.Consumer

object TestConsole {

    private val injection: SimpleMethodInjection = SimpleMethodInjection(emptyList())

    fun run(commandString: String,
            consumer: (PluginCommand) -> Unit,
            runExecutors: Boolean = true,
            sender: CommandSender = mock(Player::class.java)): PluginCommandImpl {
        val parts = if (!commandString.isBlank()) commandString.split(" ") else emptyList()
        val command = PluginCommandImpl(injection, sender, parts.toMutableList(), runExecutors)
        consumer(command)
        return command
    }

    fun run(commandString: String,
            consumer: Consumer<PluginCommand>,
            runExecutors: Boolean = true,
            sender: CommandSender = mock(Player::class.java)) = run(commandString, { consumer.accept(it) }, runExecutors, sender)
}
