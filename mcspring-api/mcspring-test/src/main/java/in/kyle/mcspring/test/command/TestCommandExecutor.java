package in.kyle.mcspring.test.command;

import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import static org.mockito.Mockito.*;

@Component
@RequiredArgsConstructor
public class TestCommandExecutor {
    
    private final TestCommandRegistration registration;
    
    public List<String> run(String command) {
        TestSender sender = spy(TestSender.class);
        run(sender, command);
        return sender.getMessages()
                .stream()
                .flatMap(s -> Arrays.stream(s.split("\n")))
                .map(s -> s.replaceAll("§.", ""))
                .collect(Collectors.toList());
    }
    
    public void run(CommandSender sender, String command) {
        command = command.trim();
        List<String> parts = new ArrayList<>(Arrays.asList(command.split(" ")));
        if (parts.get(0).isEmpty()) {
            parts.remove(0);
        }
        
        if (parts.size() != 0) {
            String label = parts.get(0);
            String[] args = parts.subList(1, parts.size()).toArray(new String[0]);
            registration.run(label, sender, label, args);
        } else {
            throw new RuntimeException("Empty command");
        }
    }
    
}
