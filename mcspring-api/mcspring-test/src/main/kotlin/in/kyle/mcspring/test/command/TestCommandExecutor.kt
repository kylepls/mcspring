package `in`.kyle.mcspring.test.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.springframework.stereotype.Component

@Component
class TestCommandExecutor(
        private val registration: TestCommandRegistration
) {

    fun makeTestPlayer(): Pair<Player, MutableList<String>> {
        val sender = mock(Player::class.java)
        val messages = mutableListOf<String>()
        doAnswer { messages.add(it.getArgument(0)); null }
                .`when`(sender).sendMessage(anyString())
        return Pair(sender, messages)
    }

    fun run(command: String): List<String> {
        val (sender, messages) = makeTestPlayer()
        run(sender, command)
        return messages
    }

    fun run(sender: CommandSender, command: String) {
        val parts = command.trim().split(" ").filter { it.isNotBlank() }.toMutableList()
        if (parts.isNotEmpty()) {
            val label = parts[0]
            val args = parts.subList(1, parts.size)
            registration.run(label, sender, label, args.toTypedArray())
        } else {
            throw RuntimeException("Empty command")
        }
    }
}
