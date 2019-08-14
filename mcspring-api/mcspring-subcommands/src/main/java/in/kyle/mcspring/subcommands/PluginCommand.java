package in.kyle.mcspring.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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

    private void ifThen(Predicate<CommandSender> ifPredicate, Executors executors, int argCount) {
        if (state == State.CLEAN && ifPredicate.test(sender)) {
            then(() -> invoke(executors, argCount));
            state = State.EXECUTED;
        }
    }

    public <T> void ifThen(Predicate<CommandSender> ifPredicate, Executors.E1<T> e1) {
        ifThen(ifPredicate, e1, 1);
    }

    public void withPlayerSender(String error) {
        if (state == State.CLEAN && !(sender instanceof Player)) {
            sendMessage(error);
            state = State.EXECUTED;
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
    
    public <T> void withMap(Map<String, T> options, Function<String, String> invalidArg) {
        if (hasExecutablePart()) {
            String part = parts.remove(0).toLowerCase();
            if (options.containsKey(part)) {
                injections.add(options.get(part));
            } else {
                String message = invalidArg.apply(part);
                sendMessage(message);
                state = State.INVALID_ARG;
            }
        } else {
            state = State.MISSING_ARG;
        }
    }
    
    public void withAny(Collection<String> options, Function<String, String> invalidArg) {
        BinaryOperator<String> merge = (a, b) -> {
            throw new RuntimeException("Duplicate option " + a);
        };
        Map<String, String> optionsMap = options.stream()
                .collect(Collectors.toMap(Function.identity(),
                                          Function.identity(),
                                          merge,
                                          LinkedHashMap::new));
        withMap(optionsMap, invalidArg);
    }
    
    public void withAny(Function<String, String> invalidArg, String... options) {
        withAny(Arrays.asList(options), invalidArg);
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
    
    public <A> void then(Executors.O0 e) {
        then(() -> invoke(e, 0));
    }
    
    public <A> void then(Executors.O1<A> e) {
        then(() -> invoke(e, 1));
    }
    
    public <A, B> void then(Executors.O2<A, B> e) {
        then(() -> invoke(e, 2));
    }
    
    public <A, B, C> void then(Executors.O3<A, B, C> e) {
        then(() -> invoke(e, 3));
    }
    
    public <A, B, C, D> void then(Executors.O4<A, B, C, D> e) {
        then(() -> invoke(e, 4));
    }
    
    public <A, B, C, D, E> void then(Executors.O5<A, B, C, D, E> e) {
        then(() -> invoke(e, 5));
    }
    
    public <A, B, C, D, E, F> void then(Executors.O6<A, B, C, D, E, F> e) {
        then(() -> invoke(e, 6));
    }
    
    public <A> void then(Executors.E1<A> e) {
        then(() -> invoke(e, 1));
    }
    
    public <A, B> void then(Executors.E2<A, B> e) {
        then(() -> invoke(e, 2));
    }
    
    public <A, B, C> void then(Executors.E3<A, B, C> e) {
        then(() -> invoke(e, 3));
    }
    
    public <A, B, C, D> void then(Executors.E4<A, B, C, D> e) {
        then(() -> invoke(e, 4));
    }
    
    public <A, B, C, D, E> void then(Executors.E5<A, B, C, D, E> e) {
        then(() -> invoke(e, 5));
    }
    
    public <A, B, C, D, E, F> void then(Executors.E6<A, B, C, D, E, F> e) {
        then(() -> invoke(e, 6));
    }
    
    public <A> void otherwise(Executors.O0 e) {
        otherwise(() -> invoke(e, 0));
    }
    
    public <A> void otherwise(Executors.O1<A> e) {
        otherwise(() -> invoke(e, 1));
    }
    
    public <A, B> void otherwise(Executors.O2<A, B> e) {
        otherwise(() -> invoke(e, 2));
    }
    
    public <A, B, C> void otherwise(Executors.O3<A, B, C> e) {
        otherwise(() -> invoke(e, 3));
    }
    
    public <A, B, C, D> void otherwise(Executors.O4<A, B, C, D> e) {
        otherwise(() -> invoke(e, 4));
    }
    
    public <A, B, C, D, E> void otherwise(Executors.O5<A, B, C, D, E> e) {
        otherwise(() -> invoke(e, 5));
    }
    
    public <A, B, C, D, E, F> void otherwise(Executors.O6<A, B, C, D, E, F> e) {
        otherwise(() -> invoke(e, 6));
    }
    
    public <A> void otherwise(Executors.E1<A> e) {
        otherwise(() -> invoke(e, 1));
    }
    
    public <A, B> void otherwise(Executors.E2<A, B> e) {
        otherwise(() -> invoke(e, 2));
    }
    
    public <A, B, C> void otherwise(Executors.E3<A, B, C> e) {
        otherwise(() -> invoke(e, 3));
    }
    
    public <A, B, C, D> void otherwise(Executors.E4<A, B, C, D> e) {
        otherwise(() -> invoke(e, 4));
    }
    
    public <A, B, C, D, E> void otherwise(Executors.E5<A, B, C, D, E> e) {
        otherwise(() -> invoke(e, 5));
    }
    
    public <A, B, C, D, E, F> void otherwise(Executors.E6<A, B, C, D, E, F> e) {
        otherwise(() -> invoke(e, 6));
    }
    
    public void otherwise(Runnable r) {
        if (state == State.MISSING_ARG || state == State.CLEAN) {
            r.run();
            state = State.EXECUTED;
        }
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
