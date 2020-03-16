package in.kyle.mcspring.subcommands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Consumer;
import java.util.function.Function;

import in.kyle.mcspring.subcommands.PluginCommandBase.State;
import in.kyle.mcspring.subcommands.tab.TabDiscovery;
import in.kyle.mcspring.test.MCSpringTest;
import lombok.var;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
public class TestTabCompletion {
    
    @Autowired
    TabDiscovery tabDiscovery;
    TestSender sender;
    
    @BeforeEach
    void setup() {
        sender = spy(TestSender.class);
    }
    
    @Test
    void testTabWithDirectExecution() {
        class Test {
            
            void root(PluginCommand command) {
                command.on("test1", this::exec);
                command.on("test2", this::exec);
                command.on("test3", this::exec);
            }
            
            void exec(String string) {
                fail("Should not run");
            }
        }
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "", test::root);
        assertThat(completions).containsExactly("test1", "test2", "test3");
    }
    
    @Test
    void testNoTab() {
        class Test {
            void root(PluginCommand command) {
                command.then(this::exec);
            }
            
            void exec(CommandSender sender) {
                fail("Should not run");
            }
        }
        
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "", test::root);
        
        assertThat(sender.getMessages()).isEmpty();
        assertThat(completions).isEmpty();
    }
    
    @Test
    void testSimpleSubs() {
        class Test {
            void root(PluginCommand command) {
                Consumer<PluginCommand> dontRun = cmd -> fail("should not run");
                command.on("a", dontRun);
                command.on("b", dontRun);
                command.on("c", dontRun);
                command.then(this::exec);
            }
            
            void exec(CommandSender sender) {
                fail("Should not run");
            }
        }
        
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "", test::root);
        
        assertThat(sender.getMessages()).isEmpty();
        assertThat(completions).containsSequence("a", "b", "c");
    }
    
    @Test
    void testSubMixed() {
        Consumer<PluginCommand> dontRun = cmd -> fail("should not run");
        class Test {
            void root(PluginCommand command) {
                command.on("a", this::cmd);
                command.on("b", this::cmd);
                command.on("c", this::exec);
                command.on("d", this::cmd);
                assertThat(command.state).isEqualTo(State.EXECUTED);
                command.otherwise(() -> dontRun.accept(null));
            }
            
            void cmd(PluginCommand command) {
            }
            
            void exec(CommandSender sender) {
                dontRun.accept(null);
            }
        }
        
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "a ", test::root);
        
        assertThat(sender.getMessages()).isEmpty();
        assertThat(completions).isEmpty();
    }
    
    @Test
    void testAny() {
        class Test {
            void root(PluginCommand command) {
                Function<String, String> f = ignored -> {
                    fail("should not run");
                    return "";
                };
                command.withAny(f, "a", "b", "c");
                command.withAny(f, "d", "e", "f");
                command.then(this::exec);
            }
            
            void exec(CommandSender sender) {
                fail("Should not run");
            }
        }
        
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "", test::root);
        
        assertThat(sender.getMessages()).isEmpty();
        assertThat(completions).containsSequence("a", "b", "c", "d", "e", "f");
        
    }
    
    @Test
    void testOnTake1() {
        class Test {
            void root(PluginCommand command) {
                Consumer<PluginCommand> doesNothing = cmd -> {
                };
                command.on("a", doesNothing);
                command.on("b", doesNothing);
                command.on("c", doesNothing);
                command.then(this::exec);
            }
            
            void exec(CommandSender sender) {
                fail("Should not run");
            }
        }
        
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "", test::root);
        assertThat(completions).containsSequence("a", "b", "c");
        
        completions = tabDiscovery.getCompletions(sender, "a", test::root);
        assertThat(completions).isEmpty();
    }
    
    @Test
    void testInvalidStop() {
        class Test {
            void root(PluginCommand command) {
                Consumer<PluginCommand> doesNothing = cmd -> fail("should not run");
                command.on("a", doesNothing);
                command.on("b", doesNothing);
                command.on("c", doesNothing);
                command.onInvalid(s -> String.format("%s is not valid", s));
                command.then(this::exec);
            }
            
            void exec(CommandSender sender) {
                fail("Should not run");
            }
        }
        
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "g", test::root);
        assertThat(completions).isEmpty();
    }
}
