package in.kyle.mcspring.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicBoolean;

import in.kyle.mcspring.subcommands.PluginCommandBase.State;
import in.kyle.mcspring.test.MCSpringTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
class TestPluginCommand {
    
    @Autowired
    TestConsole console;
    
    TestSender sender;
    
    @BeforeEach
    void setup() {
        sender = spy(TestSender.class);
    }
    
    @Test
    void testDirectExecutor() {
        class Test {
            
            void exec1(PluginCommand command) {
                command.on("test", this::handler1);
                command.on("ignore", this::handler1);
                assertThat(command.state).isEqualTo(State.EXECUTED);
                command.otherwise("ERROR");
            }
            
            void exec2(PluginCommand command) {
                command.withString();
                command.on("test2", this::handler2);
                assertThat(command.state).isEqualTo(State.EXECUTED);
                command.otherwise("ERROR");
            }
            
            void handler1(CommandSender sender) {
                sender.sendMessage("Hello World");
            }
            
            void handler2(CommandSender sender, String string) {
                sender.sendMessage(string);
            }
        }
        
        Test test = new Test();
        console.run(sender, "test", test::exec1);
        assertThat(sender.getMessages()).containsExactly("Hello World");
        
        sender.getMessages().clear();
        console.run(sender, "hello test2", test::exec2);
        assertThat(sender.getMessages()).containsExactly("hello");
    }
    
    @Test
    void testSenderArg() {
        class Test {
            void root(PluginCommand command) {
                command.then(this::exec);
            }
            
            void exec(CommandSender sender) {
                sender.sendMessage("Hello World");
            }
        }
        Test test = new Test();
        console.run(sender, "", test::root);
        assertThat(sender.getMessages()).containsExactly("Hello World");
    }
    
    @Test
    void testIf() {
        class Test {
            void root(PluginCommand command) {
                command.ifThen(ServerOperator::isOp, this::exec);
            }
            
            void exec(CommandSender sender) {
                sender.sendMessage("Works");
            }
        }
        Test test = new Test();
        console.run(sender, "", test::root);
        assertThat(sender.getMessages()).isEmpty();
        
        doReturn(true).when(sender).isOp();
        console.run(sender, "", test::root);
        assertThat(sender.getMessages()).containsExactly("Works");
    }
    
    @Test
    void testCommandSingleSentenceArg() {
        class Test {
            void root(PluginCommand command) {
                command.withSentence();
                command.then(this::exec);
            }
            
            void exec(String sentence) {
                sender.sendMessage(sentence);
            }
        }
        Test test = new Test();
        console.run(sender, "Hello to you world", test::root);
        assertThat(sender.getMessages()).containsExactly("Hello to you world");
    }
    
    @Test
    void testCommandIntArgs() {
        AtomicBoolean ran = new AtomicBoolean();
        class Test {
            void root(PluginCommand command) {
                command.withInt("error");
                command.withInt("error");
                command.withInt("error");
                command.then(this::exec);
            }
            
            void exec(int i1, int i2, int i3) {
                assertThat(i1).isEqualTo(1);
                assertThat(i2).isEqualTo(2);
                assertThat(i3).isEqualTo(3);
                ran.set(true);
            }
        }
        Test test = new Test();
        console.run(sender, "1 2 3", test::root);
        assertThat(sender.getMessages()).isEmpty();
        assertThat(ran).isTrue();
    }
    
    @Test
    void testCommandBranching() {
        class Test {
            void root(PluginCommand command) {
                command.on("a", this::a);
            }
            
            void a(PluginCommand command) {
                command.on("b", this::b);
                command.on("c", this::c);
            }
            
            private void b(PluginCommand command) {
                command.then(this::exec);
            }
            
            private void c(PluginCommand command) {
            }
            
            void exec(CommandSender sender) {
                sender.sendMessage("Works");
            }
        }
        Test test = new Test();
        console.run(sender, "a b", test::root);
        assertThat(sender.getMessages()).containsExactly("Works");
        
        sender.getMessages().clear();
        console.run(sender, "a c", test::root);
        assertThat(sender.getMessages()).isEmpty();
    }
    
    @Test
    void testOtherwise() {
        class Test {
            void root(PluginCommand command) {
                command.on("a", this::a);
                command.otherwise("no subcommand at root");
            }
            
            void a(PluginCommand command) {
                command.withInt("error");
                command.otherwise("should run if int passed or missing arg");
            }
        }
        Test test = new Test();
        console.run(sender, "", test::root);
        assertThat(sender.getMessages()).containsExactly("no subcommand at root");
        sender.getMessages().clear();
        
        console.run(sender, "a", test::root);
        assertThat(sender.getMessages()).containsExactly("should run if int passed or missing arg");
        sender.getMessages().clear();
        
        console.run(sender, "a 2", test::root);
        assertThat(sender.getMessages()).containsExactly("should run if int passed or missing arg");
    }
    
    @Test
    void testWithError() {
        class Test {
            void root(PluginCommand command) {
                command.withInt(s -> s + " is not an int");
            }
        }
        Test test = new Test();
        console.run(sender, "swag", test::root);
        assertThat(sender.getMessages()).containsExactly("swag is not an int");
    }
}
