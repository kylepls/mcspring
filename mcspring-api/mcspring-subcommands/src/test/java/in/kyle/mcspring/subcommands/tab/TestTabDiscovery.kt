package `in`.kyle.mcspring.subcommands.tab

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import `in`.kyle.mcspring.test.MCSpringTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class TestTabDiscovery {

    private val tabDiscovery = TabDiscovery(SimpleMethodInjection(emptyList()))

    class TestCommandSinglePart {
        fun base(command: PluginCommand) {
            command.withAny(listOf("sub1", "sub2", "other3"), "err")
        }
    }

    class TestCommandMultiPart {
        fun base(command: PluginCommand) {
            command.on("sub1", this::error)
            command.on("sub2", this::error)
            command.on("other3", this::other3)
        }

        private fun other3(command: PluginCommand) {
            command.withAny(listOf("other1", "other2"), "fail")
        }

        private fun error(): Unit = fail("fail");
    }

    @Test
    fun testFullCompletions() {
        getCompletions("", TestCommandSinglePart()::base).also {
            assertThat(it).containsExactly("sub1", "sub2", "other3")
        }
    }

    @Test
    fun testPartialCompletions() {
        getCompletions("sub", TestCommandSinglePart()::base).also {
            assertThat(it).containsExactly("sub1", "sub2")
        }
    }

    @Test
    fun testCompletionsMultiPart() {
        getCompletions("other3 ", TestCommandMultiPart()::base).also {
            assertThat(it).containsExactly("other1", "other2")
        }
    }

    private fun getCompletions(commandString: String, consumer: (PluginCommand) -> Unit): List<String> {
        return tabDiscovery.getCompletions(mock(Player::class.java), commandString, consumer)
    }
}
