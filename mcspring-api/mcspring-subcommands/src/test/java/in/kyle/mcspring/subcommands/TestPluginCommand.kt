package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommand
import `in`.kyle.mcspring.test.MCSpringTest
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.atomic.AtomicBoolean

@MCSpringTest
internal class TestPluginCommand {
    @Autowired
    lateinit var console: TestConsole

    lateinit var sender: Player
    lateinit var outputMessages: MutableList<String>

    @BeforeEach
    fun setup() {
        outputMessages = mutableListOf()
        sender = mock(Player::class.java)
        doAnswer { outputMessages.add(it.getArgument(0)) }.`when`(sender).sendMessage(anyString())
    }

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

        val test = Test()
        console.run(sender, "subcommand1", test::exec1)
        assertThat(outputMessages).containsExactly("handler1")
        outputMessages.clear()

        console.run(sender, "test-string subcommand2", test::exec2)
        assertThat(outputMessages).containsExactly("handler2: test-string")
    }

    @Test
    fun testSenderArg() {
        class Test {
            fun root(command: PluginCommand) = command.then(this::exec)

            fun exec(sender: CommandSender) = "Hello World"
        }

        console.run(sender, "", Test()::root)
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
    //        console.run(sender, "", test::root);
    //        assertThat(outputMessages).isEmpty();
    //
    //        sender.setOp(true);
    //        console.run(sender, "", test::root);
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

        console.run(sender, "Hello to you world", Test()::root)
        assertThat(outputMessages).containsExactly("Hello to you world")
    }

    @Test
    fun testCommandIntArgs() {
        class Test {
            fun root(command: PluginCommand) {
                command.withXYZInt("error")
                command.then(this::exec)
            }

            fun exec(x: Int, y: Int, z: Int): Boolean {
                assertThat(x).isEqualTo(1)
                assertThat(y).isEqualTo(2)
                assertThat(z).isEqualTo(3)
                return true
            }
        }

        console.run(sender, "1 2 3", Test()::root)
        assertThat(outputMessages).containsExactly("true")
    }

    @Test
    fun testCommandBranching() {
        class Test {
            fun root(command: PluginCommand) = command.on("a", this::a)

            fun a(command: PluginCommand) {
                command.on("b", this::b)
                command.on("c", this::c)
            }

            private fun b(command: PluginCommand) = command.then(this::exec)

            private fun c(command: PluginCommand) {}

            fun exec(sender: CommandSender) = "Works"
        }

        console.run(sender, "a b", Test()::root)
        assertThat(outputMessages).containsExactly("Works")
        outputMessages.clear()

        console.run(sender, "a c", Test()::root)
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
                command.withInt("error")
                command.otherwise("should run if int passed or missing arg")
            }
        }

        console.run(sender, "", Test()::root)
        assertThat(outputMessages).containsExactly("no subcommand at root")
        outputMessages.clear()

        console.run(sender, "a", Test()::root)
        assertThat(outputMessages).containsExactly("should run if int passed or missing arg")
        outputMessages.clear()
        console.run(sender, "a 2", Test()::root)
        assertThat(outputMessages).containsExactly("should run if int passed or missing arg")
    }

    @Test
    fun testWithError() {
        class Test {
            fun root(command: PluginCommand) = command.withInt { "$it is not an int" }
        }

        console.run(sender, "swag", Test()::root)
        assertThat(outputMessages).containsExactly("swag is not an int")
    }
}
