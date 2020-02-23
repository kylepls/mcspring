package in.kyle.mcspring.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.kyle.mcspring.command.SimpleMethodInjection;

public class ConceptTest {
    
    public static void main(String[] args) {
        SimpleMethodInjection injection = new SimpleMethodInjection(new ArrayList<>());
        String commandString = "set speed the speed of light";
        List<String> tp = new ArrayList<>(Arrays.asList(commandString.split(" ")));
        PluginCommand command = new PluginCommand(injection, null, tp, new ArrayList<>());
        new ConceptTest().command(command);
    }
    
    void command(PluginCommand command) {
        command.on("set", this::set);
        command.onInvalid(s -> "Subcommand not found");
        command.otherwise(() -> "subcommands: tp");
    }
    
    void set(PluginCommand command) {
        command.on("speed", this::speed);
        command.on("position", this::position);
        command.otherwise("Valid subcommands: speed");
    }
    
    void speed(PluginCommand command) {
//        command.withInt("SPeed must be an int");
        command.withSentence();
        command.then(Other::setSpeed);
        command.otherwise("Needs target arg");
    }
    
    void position(PluginCommand command) {
        command.withInt("x must be int");
        command.withInt("y must be int");
        command.withInt("z must be int");
        command.then(this::setPosition);
        command.otherwise("Need x, y, z args");
    }
    
    
    void setPosition(int x, int y, int z) {
        System.out.printf("Set position to %d,%d,%d\n", x, y, z);
    }
}
