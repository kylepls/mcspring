package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.subcommands.TestConsole.makeCommand
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandBase.State
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class TestPluginCommandWith {

    @Test
    fun testWithString() {
        val command = makeCommand("one two three")
        command.withString()
        assertThat(command.injections).containsExactly("one")
        assertThat(command.parts).containsExactly("two", "three")
    }

    @Test
    fun testWithSentence() {
        val command = makeCommand("one two three")
        command.withSentence()
        assertThat(command.injections).containsExactly("one two three")
        assertThat(command.parts).isEmpty()
    }

    @Test
    fun testWithInt() {
        val command = makeCommand("123")
        command.withInt { "" }
        assertThat(command.injections).containsExactly(123)
        assertThat(command.parts).isEmpty()
    }

    @Test
    fun testWithIntInvalid() {
        val player = mock(Player::class.java)
        val command = makeCommand("onetwothree", player)
        command.withInt { "invalid int $it" }
        assertThat(command.injections).isEmpty()
        verify(player, only()).sendMessage("invalid int onetwothree")
    }

    @Test
    fun testWithDouble() {
        val command = makeCommand("123.456")
        command.withDouble { "" }
        assertThat(command.injections).containsExactly(123.456)
        assertThat(command.parts).isEmpty()
    }

    @Test
    fun testWithDoubleInvalid() {
        val player = mock(Player::class.java)
        val command = makeCommand("onetwothree.fourfivesix", player)
        command.withDouble { "invalid double $it" }
        assertThat(command.injections).isEmpty()
        verify(player, only()).sendMessage("invalid double onetwothree.fourfivesix")
    }

    @Test
    fun testWithMap() {
        val options = mapOf("optionA" to 'a', "optionB" to 'b', "optionC" to 'c')
        val command = makeCommand("optionB")
        command.withMap(options) { "invalid option $it" }
        assertThat(command.injections).containsExactly('b')
        assertThat(command.parts).isEmpty()
    }

    @Test
    fun testWithAny() {
        val options = listOf("optionA", "optionB", "optionC")
        val command = makeCommand("optionB")
        command.withAny(options) { "invalid option $it" }
        assertThat(command.injections).containsExactly("optionB")
        assertThat(command.parts).isEmpty()
    }

    @Test
    fun testWith() {
        var command = makeCommand("option")
        command.with({ "output" })
        assertThat(command.injections).containsExactly("output")
        assertThat(command.parts).isEmpty()
        assertThat(command.state).isEqualTo(State.CLEAN)

        command = makeCommand("option")
        command.with({ null })
        assertThat(command.injections).isEmpty()
        assertThat(command.parts).containsExactly("option")
        assertThat(command.state).isEqualTo(State.COMPLETED)
    }

    @Test
    fun testWithNoOption() {
        val command = makeCommand("")
        command.with({ "output" })
        assertThat(command.injections).isEmpty()
        assertThat(command.parts).isEmpty()
        assertThat(command.state).isEqualTo(State.MISSING_ARG)
    }
}
