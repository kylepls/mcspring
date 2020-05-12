package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.subcommands.TestConsole.makeCommand
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandBase.State
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

class TestPluginCommandExecutors {

    @Test
    fun testOtherwise() {
        val player = mock(Player::class.java)
        val command = makeCommand("", player)
        command.otherwise("output")
        verify(player, only()).sendMessage("output")
    }

    @Test
    fun testOn() {
        val player = mock(Player::class.java)
        val command = makeCommand("optionB", player)
        command.on("optionA", TestClass()::fail)
        command.on("optionB", TestClass()::execute)
        verify(player, only()).sendMessage("output")
    }

    @Test
    fun testOnMissingPart() {
        val player = mock(Player::class.java)
        val command = makeCommand("", player)
        command.on("optionA", TestClass()::fail)
        command.on("optionB", TestClass()::fail)
        verify(player, never()).sendMessage(anyString())
        assertThat(command.state).isEqualTo(State.CLEAN)
    }

    @Test
    fun testOnInvalid() {
        val player = mock(Player::class.java)
        val command = makeCommand("invalidOption", player)

        command.on("optionA", TestClass()::fail)
        command.on("optionB", TestClass()::fail)
        assertThat(command.state).isEqualTo(State.CLEAN)
        command.onInvalid { "no valid option $it" }
        assertThat(command.state).isEqualTo(State.COMPLETED)

        verify(player, only()).sendMessage("no valid option invalidOption")
    }

    class TestClass {
        fun fail(): Any = fail()
        fun execute() = "output"
    }
}
