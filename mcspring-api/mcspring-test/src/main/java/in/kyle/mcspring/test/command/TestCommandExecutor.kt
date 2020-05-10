package `in`.kyle.mcspring.test.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.springframework.stereotype.Component
import java.util.*

@Component
class TestCommandExecutor(
        val registration: TestCommandRegistration
) {
    fun run(command: String): List<String> {
        val sender = Mockito.mock(Player::class.java)
        val messages = ArrayList<String>()
        Mockito.doAnswer { invocationOnMock: InvocationOnMock ->
            messages.add(invocationOnMock.getArgument(0))
            null
        }.`when`(sender).sendMessage(ArgumentMatchers.anyString())
        run(sender, command)
        return messages
    }

    fun run(sender: CommandSender?, command: String) {
        val command = command.trim { it <= ' ' }
        val parts = command.split(" ").toMutableList()
        if (parts[0].isEmpty()) {
            parts.removeAt(0)
        }
        if (parts.size != 0) {
            val label = parts[0]
            val args = parts.subList(1, parts.size).toTypedArray()
            registration.run(label, sender!!, label, args)
        } else {
            throw RuntimeException("Empty command")
        }
    }
}
