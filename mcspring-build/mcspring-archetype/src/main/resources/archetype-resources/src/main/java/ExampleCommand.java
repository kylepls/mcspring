package ${groupId};

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import in.kyle.mcspring.command.Command;

// Remember to annotate Spring beans with @Component
// If you forget this, your class will not run.
@Component
class ExampleCommand {
    // Do not extend JavaPlugin, you will regret it
    // To get an instance of Plugin, have Spring inject it.
    
    // Use this in-place of an onEnable method. You can make as many of these methods as you like.
    // for onDisable see the @PreDestroy annotation.
    @PostConstruct
    void onEnable() {
    
    }
    
    // Commands will be automatically set up. Do not create a plugin.yml
    // The return value of a command method will be sent to the CommandSender using Object::toString
    // void methods simplly will not send a message
    @Command("test")
    String test() {
        return "The command works!";
    }
}
