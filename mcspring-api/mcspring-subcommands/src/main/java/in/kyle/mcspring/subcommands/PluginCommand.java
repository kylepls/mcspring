package in.kyle.mcspring.subcommands;

import in.kyle.mcspring.command.SimpleMethodInjection;
import org.bukkit.command.CommandSender;

import java.util.List;

public class PluginCommand extends PluginCommandBase {

    public PluginCommand(SimpleMethodInjection injection,
                         CommandSender sender,
                         List<String> parts,
                         List<Object> injections) {
        super(injection, sender, parts, injections);
    }

    public <A> void on(String command, Executors.O1<A> e) {
        callOn(command, e, 1);
    }

    public <A, B> void on(String command, Executors.O2<A, B> e) {
        callOn(command, e, 2);
    }

    public <A, B, C> void on(String command, Executors.O3<A, B, C> e) {
        callOn(command, e, 3);
    }

    public <A, B, C, D> void on(String command, Executors.O4<A, B, C, D> e) {
        callOn(command, e, 4);
    }

    public <A, B, C, D, E> void on(String command, Executors.O5<A, B, C, D, E> e) {
        callOn(command, e, 5);
    }

    public <A, B, C, D, E, F> void on(String command, Executors.O6<A, B, C, D, E, F> e) {
        callOn(command, e, 6);
    }

    public void on(String command, Executors.O0 executors) {
        callOn(command, executors, 0);
    }

    public <A> void on(String command, Executors.E1<A> e) {
        callOn(command, e, 1);
    }

    public <A, B> void on(String command, Executors.E2<A, B> e) {
        callOn(command, e, 2);
    }

    public <A, B, C> void on(String command, Executors.E3<A, B, C> e) {
        callOn(command, e, 3);
    }

    public <A, B, C, D> void on(String command, Executors.E4<A, B, C, D> e) {
        callOn(command, e, 4);
    }

    public <A, B, C, D, E> void on(String command, Executors.E5<A, B, C, D, E> e) {
        callOn(command, e, 5);
    }

    public <A, B, C, D, E, F> void on(String command, Executors.E6<A, B, C, D, E, F> e) {
        callOn(command, e, 6);
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
}
