package `in`.kyle.mcspring.commands.dsl

import `in`.kyle.mcspring.commands.dsl.util.CommandUtils
import org.bukkit.entity.Player
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock

class TestException : RuntimeException()

object CommandTestSupport {

    fun runCommand(command: String, lambda: CommandBuilder.() -> Unit): String {
        val (context, messages) = makeContext(command)
        val provider = command(lambda).provider
        CommandUtils.runCommand(context, provider)
        return messages.joinToString("\n")
    }

    fun getCompletions(command: String, lambda: CommandBuilder.() -> Unit): List<String> {
        val (context, _) = makeContext(command)
        val provider = command(lambda).provider
        return CommandUtils.getCompletions(context, provider)
    }

    fun makeContext(
            command: String,
            runExecutors: Boolean = true
    ): Pair<CommandContext, MutableList<String>> {
        val (player, messages) = makeTestPlayer()
        val args = command.split(" ").filter { it.isNotBlank() }
        val context = CommandContext(
                player,
                args.getOrElse(0) { "" },
                args.toMutableList(),
                runExecutors = runExecutors
        )
        return Pair(context, messages)
    }

    private fun makeTestPlayer(): Pair<Player, MutableList<String>> {
        val sender = mock(Player::class.java)
        val messages = mutableListOf<String>()
        doAnswer { messages.add(it.getArgument(0)); null }
                .`when`(sender)
                .sendMessage(ArgumentMatchers.anyString())
        return Pair(sender, messages)
    }

    fun makeBuilder(command: String) = CommandBuilder(makeContext(command).first)
}
