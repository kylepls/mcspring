package in.kyle.mcspring.subcommands;

import in.kyle.api.bukkit.entity.TestPlayer;
import in.kyle.mcspring.SpringSpigotSupport;
import in.kyle.mcspring.subcommands.tab.TabDiscovery;
import lombok.var;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(classes = SpringSpigotSupport.class)
public class TestTabCompletion {

    @Autowired
    TestPlayer sender;
    @Autowired
    TabDiscovery tabDiscovery;
    List<String> outputMessages;

    @BeforeEach
    void setup() {
        outputMessages = new ArrayList<>();
        sender.getMessages().subscribe(outputMessages::add);
    }

    @Test
    void testTabWithDirectExecution() {
        class Test {

            void root(PluginCommand command) {
                command.on("test", this::exec);
            }

            void exec(String string) {
                fail("Should not run");
            }
        }
        Test test = new Test();
        var completions = tabDiscovery.getCompletions(sender, "", test::root);
        assertThat(completions).containsExactly("test");
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

        assertThat(outputMessages).isEmpty();
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

        assertThat(outputMessages).isEmpty();
        assertThat(completions).containsSequence("a", "b", "c");
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

        assertThat(outputMessages).isEmpty();
        assertThat(completions).containsSequence("a", "b", "c", "d", "e", "f");

    }

    @Test
    void testOnTake1() {
        class Test {
            void root(PluginCommand command) {
                Consumer<PluginCommand> doesNothing = cmd -> {};
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
