package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.subcommands.TestConsole.run
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class TestPluginCommand {

    @Test
    fun testDirectExecutor() {
        class Test {
            fun exec1(command: PluginCommand) = command.on("subcommand1", this::handler1)
            fun exec2(command: PluginCommand) {
                command.withString()
                command.on("subcommand2", this::handler2)
            }

            fun handler1() = "handler1"
            fun handler2(string: String) = "handler2: $string"
        }

        val (sender, outputMessages) = makePlayer()
        run("subcommand1", Test()::exec1, true, sender)
        assertThat(outputMessages).containsExactly("handler1")
        outputMessages.clear()

        run("test-string subcommand2", Test()::exec2, sender = sender)
        assertThat(outputMessages).containsExactly("handler2: test-string")
    }

    @Test
    fun testSenderArg() {
        class Test {
            fun root(command: PluginCommand) = command.then(this::exec)

            @Suppress("UNUSED_PARAMETER")
            fun exec(sender: CommandSender) = "Hello World"
        }

        val (sender, outputMessages) = makePlayer()
        run("", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("Hello World")
    }

    //    @Test
    //    void testIf() {
    //        class Test {
    //            void root(PluginCommand command) {
    //                command.re(ServerOperator::isOp, this::exec);
    //            }
    //
    //            void exec(CommandSender sender) {
    //                sender.sendMessage("Works");
    //            }
    //        }
    //        Test test = new Test();
    //        run(sender, "", test::root);
    //        assertThat(outputMessages).isEmpty();
    //
    //        sender.setOp(true);
    //        run(sender, "", test::root);
    //        assertThat(outputMessages).containsExactly("Works");
    //    }

    @Test
    fun testCommandSingleSentenceArg() {
        class Test {
            fun root(command: PluginCommand) {
                command.withSentence()
                command.then(this::exec)
            }

            fun exec(sentence: String) = sentence
        }

        val (sender, outputMessages) = makePlayer()
        run("Hello to you world", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("Hello to you world")
    }

    @Test
    fun testCommandIntArgs() {
        class Test {
            fun root(command: PluginCommand) {
                command.withXYZInt { "error" }
                command.then(this::exec)
            }

            fun exec(x: Int, y: Int, z: Int): Boolean {
                assertThat(x).isEqualTo(1)
                assertThat(y).isEqualTo(2)
                assertThat(z).isEqualTo(3)
                return true
            }
        }

        val (sender, outputMessages) = makePlayer()
        run("1 2 3", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("true")
    }

    @Test
    fun testCommandBranching() {
        class Test {
            fun root(command: PluginCommand) = command.on("a", this::a)

            private fun a(command: PluginCommand) {
                command.on("b", this::b)
                command.on("c", this::c)
            }

            private fun b(command: PluginCommand) = command.then(this::exec)

            @Suppress("UNUSED_PARAMETER")
            private fun c(command: PluginCommand) {
            }

            private fun exec() = "Works"
        }

        val (sender, outputMessages) = makePlayer()
        run("a b", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("Works")
        outputMessages.clear()

        run("a c", Test()::root, sender = sender)
        assertThat(outputMessages).isEmpty()
    }

    @Test
    fun testOtherwise() {
        class Test {
            fun root(command: PluginCommand) {
                command.on("a", this::a)
                command.otherwise("no subcommand at root")
            }

            fun a(command: PluginCommand) {
                command.withInt { "error" }
                command.otherwise("should run if int passed or missing arg")
            }
        }

        val (sender, outputMessages) = makePlayer()
        run("", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("no subcommand at root")
        outputMessages.clear()

        run("a", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("should run if int passed or missing arg")
        outputMessages.clear()
        run("a 2", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("should run if int passed or missing arg")
    }

    @Test
    fun testWithError() {
        class Test {
            fun root(command: PluginCommand) = command.withInt { "$it is not an int" }
        }

        val (sender, outputMessages) = makePlayer()
        run("swag", Test()::root, sender = sender)
        assertThat(outputMessages).containsExactly("swag is not an int")
    }

    private fun makePlayer(): Pair<Player, MutableList<String>> {
        val sender = mock(Player::class.java)
        val messages = mutableListOf<String>()
        doAnswer { messages.add(it.getArgument(0)) }.`when`(sender).sendMessage(anyString())
        return Pair(sender, messages)
    }
}
