package `in`.kyle.mcspring.commands.dsl

import org.bukkit.entity.Player
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock

object CommandTestSupport {

    fun runCommand(command: String, lambda: CommandBuilder.() -> Unit): String {
        val (context, messages) = makeContext(command)
        commandExecutor(lambda).provider(context)
        return messages.joinToString("\n")
    }

    fun makeContext(command: String): Pair<CommandContext, MutableList<String>> {
        val (player, messages) = makeTestPlayer()
        val args = command.split(" ").filter { it.isNotBlank() }
        val context = CommandContext(player, args.getOrElse(0) { "" }, args.toMutableList())
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
}
