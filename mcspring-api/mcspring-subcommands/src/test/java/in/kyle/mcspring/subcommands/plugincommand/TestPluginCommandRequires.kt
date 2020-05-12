package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.subcommands.TestConsole.makeCommand
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandBase.State
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

class TestPluginCommandRequires {
    @Test
    fun testRequires() {
        val player = mock(Player::class.java)
        val command = makeCommand("test", player)
        command.requires(true) {"error"}
        verify(player, never()).sendMessage(anyString())

        command.requires(false) {"error"}
        verify(player, only()).sendMessage("error")
    }

    @Test
    fun testRequiresPlayerSender() {
        val player = mock(Player::class.java)
        val command = makeCommand("test", player)
        command.requiresPlayerSender { "error" }
        verify(player, never()).sendMessage(anyString())
        assertThat(command.state).isEqualTo(State.CLEAN)
    }

    @Test
    fun testRequiresPlayerSenderFail() {
        val console = mock(ConsoleCommandSender::class.java)
        val command = makeCommand("test", console)
        command.requiresPlayerSender { "error" }
        verify(console, only()).sendMessage("error")
        assertThat(command.state).isEqualTo(State.COMPLETED)
    }
}
