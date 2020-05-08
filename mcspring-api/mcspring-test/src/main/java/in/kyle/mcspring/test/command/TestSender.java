package in.kyle.mcspring.test.command;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public abstract class TestSender implements Player {
    
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
