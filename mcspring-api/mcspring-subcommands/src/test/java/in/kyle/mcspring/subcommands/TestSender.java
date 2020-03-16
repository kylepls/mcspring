package in.kyle.mcspring.subcommands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public abstract class TestSender implements CommandSender {
    
    @Getter
    private final List<String> messages = new ArrayList<>();
    
    @Override
    public void sendMessage(String message) {
        messages.add(message);
    }
    
    @Override
    public void sendMessage(String[] messages) {
        Arrays.stream(messages).forEach(this::sendMessage);
    }
}
