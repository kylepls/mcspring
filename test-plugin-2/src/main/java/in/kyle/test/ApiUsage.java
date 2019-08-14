package in.kyle.test;

import com.joshb.mcspringboot.command.Command;
import com.joshb.testploogin.TestApi;

import org.bukkit.Bukkit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiUsage {
    
    private final TestApi api;
    
    @Command("get")
    public String get() {
        return String.format("The value is %s", api.getValue());
    }
    
    @Scheduled(fixedRate = 1000)
    public void interval() {
        Bukkit.broadcastMessage(api.getValue());
    }
}
