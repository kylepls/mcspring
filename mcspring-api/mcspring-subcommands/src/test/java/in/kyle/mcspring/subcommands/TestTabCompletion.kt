package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.subcommands.TestConsole.run
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Test

internal class TestTabCompletion {

    @Test
    fun testWithString() {
        class Test {
            fun base(command: PluginCommand) {
                command.withString()
                command.withString()
            }
        }

        val command = run("hello world", Test()::base, runExecutors = false)

        assertThat(command.completions).hasSize(2)
        assertThat(command.completions.first().completions).isEmpty()
        assertThat(command.completions.last().completions).isEmpty()
    }

    @Test
    fun testThenNoExecute() {
        class Test {
            fun base(command: PluginCommand) {
                command.then(this::fail)
            }

            fun fail(): Unit = fail("Should not run")
        }

        run("", Test()::base, runExecutors = false)
    }

    @Test
    fun testOnRecurse() {
        class Test {
            fun base(command: PluginCommand) {
                command.on("subcommand-1", this::sub1)
                command.on("other-1", this::fail)
                command.on("other-2", this::fail)
            }

            fun sub1(command: PluginCommand) {
                command.on("subcommand-2", this::fail)
            }

            fun fail(): Unit = fail("Should not run")
        }

        val command = run("subcommand-1 subcommand-2", Test()::base, runExecutors = false)
        assertThat(command.completions).hasSize(1)
        assertThat(command.completions[0].completions).hasSize(3)
        assertThat(command.child).isNotNull()
        assertThat(command.child!!.completions).hasSize(1)
    }

    @Test
    fun testMultiOn() {
        class Test {
            fun base(command: PluginCommand) {
                command.on("a", this::fail)
                command.on("b", this::fail)
                command.on("c", this::fail)
            }

            fun fail(): Unit = fail()
        }

        val command = run("", Test()::base, runExecutors = false)
        assertThat(command.completions).hasSize(1)
        assertThat(command.completions[0].completions).containsExactly("a", "b", "c")
    }

    @Test
    fun testWithAny() {
        class Test {
            fun base(command: PluginCommand) = command.withAny(listOf("a", "b", "c")) { "" }
        }

        val command = run("", Test()::base, runExecutors = false)
        assertThat(command.completions).hasSize(1)
        assertThat(command.completions[0].completions).containsExactly("a", "b", "c")
    }
}
