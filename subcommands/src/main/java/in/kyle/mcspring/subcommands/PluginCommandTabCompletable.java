package in.kyle.mcspring.subcommands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import in.kyle.mcspring.command.SimpleMethodInjection;
import lombok.Getter;
import lombok.var;

public class PluginCommandTabCompletable extends PluginCommand {
    
    @Getter
    private final List<String> tabCompletionOptions = new ArrayList<>();
    @Getter
    private PluginCommandTabCompletable child;
    
    public PluginCommandTabCompletable(SimpleMethodInjection injection,
                                       CommandSender sender,
                                       List<String> parts) {
        super(injection, sender, parts, Collections.emptyList());
    }
    
    public State getState() {
        return state;
    }
    
    public boolean hasChild() {
        return child != null;
    }
    
    @Override
    public void on(String command, Consumer<PluginCommand> executor) {
        tabCompletionOptions.add(command);
        super.on(command, executor);
    }
    
    @Override
    public void onInvalid(Function<String, String> help) {
        state = State.INVALID_ARG;
    }
    
    @Override
    public <T> void with(Function<String, Optional<T>> processor, Function<String, String> error) {
        tabCompletionOptions.clear();
        if (hasExecutablePart()) {
            parts.remove(0);
            state = State.INVALID_ARG;
        } else {
            state = State.MISSING_ARG;
        }
    }
    
    @Override
    public void withAny(Supplier<List<String>> stringOptions, Function<String, String> invalidArg) {
        if (hasExecutablePart()) {
            List<String> validOptions = stringOptions.get();
            String part = parts.remove(0).toLowerCase();
            if (!validOptions.contains(part)) {
                state = State.INVALID_ARG;
            }
        } else {
            state = State.MISSING_ARG;
            tabCompletionOptions.addAll(stringOptions.get());
        }
    }
    
    @Override
    public void otherwise(Supplier<String> supplier) {
    }

    
    @Override
    public void then(Runnable r) {
    }
    
    @Override
    PluginCommand copy() {
        var command =
                new PluginCommandTabCompletable(injection, sender, parts);
        if (child == null) {
            child = command;
            return command;
        } else {
            throw new IllegalStateException("Command cannot have 2 children");
        }
    }
}
