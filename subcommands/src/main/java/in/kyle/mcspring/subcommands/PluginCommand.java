package in.kyle.mcspring.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import in.kyle.mcspring.command.SimpleMethodInjection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class PluginCommand {
    
    final SimpleMethodInjection injection;
    final CommandSender sender;
    final List<String> parts;
    final List<Object> injections;
    State state = State.CLEAN;
    
    public void on(String command, Consumer<PluginCommand> executor) {
        if (hasExecutablePart()) {
            String part = parts.get(0);
            if (command.equalsIgnoreCase(part)) {
                parts.remove(0);
                executor.accept(copy());
                state = State.EXECUTED;
            }
        }
    }
    
    public void onInvalid(Function<String, String> help) {
        if (hasExecutablePart()) {
            String message = help.apply(parts.get(0));
            sendMessage(message);
            state = State.EXECUTED;
        }
    }
    
    public void otherwise(String message) {
        otherwise(() -> message);
    }
    
    public void otherwise(Supplier<String> supplier) {
        if (state == State.MISSING_ARG || state == State.CLEAN) {
            String message = supplier.get();
            sendMessage(message);
            state = State.EXECUTED;
        }
    }
    
    public <T> void with(Function<String, Optional<T>> processor, Function<String, String> error) {
        if (hasExecutablePart()) {
            String part = parts.remove(0);
            Optional<T> apply = processor.apply(part);
            if (apply.isPresent()) {
                injections.add(apply.get());
            } else {
                String message = error.apply(part);
                sendMessage(message);
                state = State.INVALID_ARG;
            }
        } else {
            state = State.MISSING_ARG;
        }
    }
    
    public void withString() {
        with(Optional::of, null);
    }
    
    public void withSentence() {
        if (hasExecutablePart()) {
            String part = String.join(" ", parts);
            injections.add(part);
            parts.clear();
        }
    }
    public void withOfflinePlayer(String notPlayer) {
        withOfflinePlayer(s -> notPlayer);
    }
    
    public void withOfflinePlayer(Function<String, String> notPlayer) {
        with(s -> Optional.ofNullable(Bukkit.getOfflinePlayer(s)), notPlayer);
    }
    
    public void withPlayer(String notPlayer) {
        withPlayer(s -> notPlayer);
    }
    
    public void withPlayer(Function<String, String> notPlayer) {
        with(s -> Optional.ofNullable(Bukkit.getPlayerExact(s)), notPlayer);
    }
    
    public void withWorld(String notWorld) {
        withWorld(s -> notWorld);
    }
    
    public void withWorld(Function<String, String> notWorld) {
        with(s -> Bukkit.getWorlds()
                .stream()
                .filter(w -> w.getName().equalsIgnoreCase(s))
                .findFirst(), notWorld);
    }
    
    public void withAny(Function<String, String> invalidArg, String... options) {
        withAny(Arrays.asList(options), invalidArg);
    }
    
    public void withAny(List<String> options, Function<String, String> invalidArg) {
        withAny(() -> options, invalidArg);
    }
    
    public void withAny(Supplier<List<String>> options, Function<String, String> invalidArg) {
        if (hasExecutablePart()) {
            List<String> validOptions = options.get();
            String part = parts.remove(0).toLowerCase();
            if (validOptions.contains(part)) {
                injections.add(part);
            } else {
                String message = invalidArg.apply(part);
                sendMessage(message);
                state = State.INVALID_ARG;
            }
        } else {
            state = State.MISSING_ARG;
        }
    }
    
    public void withOnlinePlayer(Function<String, String> playerNotFound) {
        with(s -> Optional.ofNullable(Bukkit.getPlayer(s)), playerNotFound);
    }
    
    public void withInt(String notInteger) {
        withInt(a -> notInteger);
    }
    
    public void withInt(Function<String, String> notInteger) {
        with(this::tryParseInt, notInteger);
    }
    
    public void withDouble(String notDouble) {
        withDouble(a -> notDouble);
    }
    
    public void withDouble(Function<String, String> notDouble) {
        with(this::tryParseDouble, notDouble);
    }
    
    private Optional<Integer> tryParseInt(String intString) {
        try {
            return Optional.of(Integer.parseInt(intString));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    private Optional<Double> tryParseDouble(String intString) {
        try {
            return Optional.of(Double.parseDouble(intString));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    @SneakyThrows
    public <A> void then(Executors.O0 e) {
        then(() -> invoke(e, 0));
    }
    
    @SneakyThrows
    public <A> void then(Executors.O1<A> e) {
        then(() -> invoke(e, 1));
    }
    
    @SneakyThrows
    public <A, B> void then(Executors.O2<A, B> e) {
        then(() -> invoke(e, 2));
    }
    
    @SneakyThrows
    public <A, B, C> void then(Executors.O3<A, B, C> e) {
        then(() -> invoke(e, 3));
    }
    
    @SneakyThrows
    public <A, B, C, D> void then(Executors.O4<A, B, C, D> e) {
        then(() -> invoke(e, 4));
    }
    
    @SneakyThrows
    public <A, B, C, D, E> void then(Executors.O5<A, B, C, D, E> e) {
        then(() -> invoke(e, 5));
    }
    
    @SneakyThrows
    public <A, B, C, D, E, F> void then(Executors.O6<A, B, C, D, E, F> e) {
        then(() -> invoke(e, 6));
    }
    
    @SneakyThrows
    public <A> void then(Executors.E1<A> e) {
        then(() -> invoke(e, 1));
    }
    
    @SneakyThrows
    public <A, B> void then(Executors.E2<A, B> e) {
        then(() -> invoke(e, 2));
    }
    
    @SneakyThrows
    public <A, B, C> void then(Executors.E3<A, B, C> e) {
        then(() -> invoke(e, 3));
    }
    
    @SneakyThrows
    public <A, B, C, D> void then(Executors.E4<A, B, C, D> e) {
        then(() -> invoke(e, 4));
    }
    
    @SneakyThrows
    public <A, B, C, D, E> void then(Executors.E5<A, B, C, D, E> e) {
        then(() -> invoke(e, 5));
    }
    
    @SneakyThrows
    public <A, B, C, D, E, F> void then(Executors.E6<A, B, C, D, E, F> e) {
        then(() -> invoke(e, 6));
    }
    
    public void then(Runnable r) {
        if (parts.size() == 0 && state == State.CLEAN) {
            r.run();
            state = State.EXECUTED;
        }
    }
    
    void sendMessage(String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    PluginCommand copy() {
        return new PluginCommand(injection, sender, parts, injections);
    }
    
    @SneakyThrows
    private void invoke(Executors executors, int argCount) {
        Method method = executors.getMethod(argCount);
        
        injections.add(sender);
        Object[] objects = injections.toArray(new Object[0]);
        Object[] parameters = injection.getParameters(method, Collections.emptyList(), objects);
        
        Method handleMethod = getHandleMethod(executors);
        handleMethod.setAccessible(true);
        Object output = handleMethod.invoke(executors, parameters);
        if (output != null) {
            sendMessage(output.toString());
        }
    }
    
    boolean hasExecutablePart() {
        return parts.size() > 0 && state == State.CLEAN;
    }
    
    private Method getHandleMethod(Executors executors) {
        return Stream.of(executors.getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals("handle"))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
    
    public enum State {
        CLEAN,
        MISSING_ARG,
        INVALID_ARG,
        EXECUTED
    }
}
