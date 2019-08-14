package in.kyle.mcspring.test.command;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import in.kyle.api.bukkit.TestCommandSender;
import in.kyle.api.bukkit.entity.TestPlayer;
import in.kyle.api.generate.api.Generator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestCommandExecutor {
    
    private final TestCommandRegistration registration;
    
    public List<String> run(String command) {
        TestPlayer player = Generator.create().create(TestPlayer.class);
        return run(player, command);
    }
    
    public List<String> run(TestCommandSender sender, String command) {
        command = command.trim();
        List<String> parts = new ArrayList<>(Arrays.asList(command.split(" ")));
        if (parts.get(0).isEmpty()) {
            parts.remove(0);
        }
        
        if (parts.size() != 0) {
            String label = parts.get(0);
            String[] args = parts.subList(1, parts.size()).toArray(new String[0]);
            
            List<String> output = new ArrayList<>();
            sender.getMessages().subscribe(output::add);
            
            registration.run(label, sender, label, args);
            
            return output.stream()
                    .flatMap(s -> Arrays.stream(s.split("\n")))
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Empty command");
        }
    }
}
