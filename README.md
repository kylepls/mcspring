# mcspring [![Build Status](https://travis-ci.org/kylepls/mcspring.svg?branch=master)](https://travis-ci.org/kylepls/mcspring) ![Maven Central](https://img.shields.io/maven-central/v/in.kyle.mcspring/mcspring)

Writing Bukkit plugins is a nightmare. I often lay awake in my bed late at night unable to sleep
 because Bukkit made events an annotation but commands are created by implementing a class. 
 The plugin.yml is useless and main classes that extend JavaPlugin are cluttered piles of shit. 
 
 {insert your horror story/gripe here}
  
These are solved problems. Spring Boot took care of this issue ages ago. 
So how about we ditch this ridiculous programming model and hop on the Spring train.

```java
@Component
class Test { // We don't have to extend JavaPlugin. The plugin.yml is also generated for us.
    
    @Command("test")
    String playerSender(Player sender, String command) {
        // parameters are automatically injected
        // injects: Player, Label, String[] args (no specific order required)
        // also injects any Spring beans such as Plugin (no specific order required)
        return command + " works!";
    }
    
    @Scheduled(fixedDelay = 10000)
    void interval() { // runs every 10 seconds
        Bukkit.broadcastMessage("REMEMBER TO DONATE");
    }
    
    @EventHandler
    void onMove(PlayerMoveEvent e) { // Events automatically registered
        getLogger().info(e.getPlayer().getName() + " moved");
    }
    
    // sub-commands example
    // first we define the structure of the command (how it's parsed)
    // then we define the execution of the command (how it runs)
    // structure `/plot tp 10 20`
    @Command("plot")
    void plot(PluginCommand command) {
        command.on("tp", this::plotTp); // calls this method when "tp" is passed
        command.otherwise("Usage: plot <tp>"); // if no methods were called, fallback to this message
    }
    
    private void tp(PluginCommand command) {
        command.withInt("Parameter must be an integer"); // parse 1 integer from the command, otherwise show the message parameter
        command.withInt("Parameter must be an integer"); // parse 1 integer from the command, otherwise show the message parameter
        command.then(this::executeTp); // if everything is okay so far, run the executor
        command.otherwise("Usage: plot tp <x> <y>"); // if not enough args (or too many) were passed, show this message
    }

    // parameters are injected from the #with arguments
    // injects the CommandSender, Label, and String[] args
    // Spring beans are also injected    
    private void executeTp(Player sender, int x, int y) {
        // sender corresponds to the player that sent the command, the argument position doesn't matter
        // x and y correspond to the 2 parameters that were parsed using the #withInt method
        sender.teleportToPlot(x, y);
    }    
}
```

---

## What's in the sauce?
* Main plugin class is generated automatically, you don't need to interact with it.
* The `plugin.yml` is also a thing of the past. May it rest in peace.
* Have two plugins? Want to share a Bean or two? Go for it. It's all taken care of.
* Commands are now registered with `@Command`. Put it anywhere and forget about it.
* Schedulers are defined with `@Scheduler`. Another thing to schlep away somewhere.
* `@EventHandler` now registers itself. About damn time.
* Like money? Vault support is in the box `in.kyle.mcspring.economy.EconomyService`
* Want my hot take on sub-command handing? We've got you covered (see the wiki)

## Getting Started
I went ahead and wrote a full tutorial series for you newcomers. Get started [here](https://github.com/kylepls/mcspring/wiki/Getting-Setup)

If you think you're too smart for the beginner tutorial; go to the 
[wiki](https://github.com/kylepls/mcspring/wiki) and piece it together.

If you're really really smart; check out the example plugins in the `mcspring-example` folder.

---

##### Final Notes
Thanks to https://github.com/Alan-Gomes/mcspring-boot/ for the inspiration of this project!
