package ${groupId};

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import in.kyle.mcspring.command.Command;

@Component
class ExampleCommand {
    
    @PostConstruct
    void onEnable() {
    
    }
    
    @Command("test")
    String test() {
        return "The command works!";
    }
}
