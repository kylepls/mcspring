[![Build Status](https://travis-ci.org/kylepls/mcspring.svg?branch=master)](https://travis-ci.org/kylepls/mcspring)
![Maven Central](https://img.shields.io/maven-central/v/in.kyle.mcspring/mcspring)


### mc-spring
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

#### Features

* No main plugin class needed, ever.
* No plugin.yml needed, ever.
* Cross-plugin injection
* Automatic command registration
* Automatic scheduler registration
* Automatic listener registration (no listener interface needed)
* Injection of common Bukkit objects as beans
  * Plugin
  * Server
  * PluginManager
  * ...
* Optional vault support via (`in.kyle.mcspring.economy.EconomyService`)
* Super sleek sub-comands API

---
#### Maven Setup

I tried to make this as easy as possible. If you're using Intellij just do the following.

1. Create a new project
2. In the create dialog select `Maven -> create from archetype`
3. Click `Add archetype` on the right-hand side
4. Type in the following:

    1. group: `in.kyle.mcspring` 
    2. artifact: `archetype` 
    3. version: `0.0.6` 

Then a project will be created for you with a project-specific Spigot folder already setup for you.

Next:

1. Run a Maven install to create the required files
2. Create a new JAR run configuration
3. Select the downloaded `spigot.jar` in the `spigot` folder as the target
4. Change the run environment to the `spigot` folder
5. Add the following **VM flag** `-DIReallyKnowWhatIAmDoingISwear`
6. To update the plugin on the server, just run a maven install and restart the server.

##### Main class

You don't need a main class. 
Don't bother with the `plugin.yml` either, mcspring will take care of that. 
Instead of using the `onEnable` and `onDisable` methods use the `@PostConstract` and `@PreDestroy` annotations.

This will ensure that Spring is able to be properly initialized.

If you need to add a dependency to the `plugin.yml` just use the `@PluginDepends` annotation
 somewhere in your project. If you care about versions, plugin names, or descriptions check out
the `@SpringPlugin` annotation. Finally, there is the `@PluginAuthor` annotation.

The `plugin.yml` is generated for you. Don't worry about it.

**Do Not**: 
* Use `@Autowired`. It's gross. Save it for your tests.
* Create a constructor in the main plugin class. Bukkit still loads that.
* Create cyclic dependencies between plugins. I promise you there will be errors.

--- 

#### Other Notes

##### Do not duplicate commands. For example:
```java
@Command("test")
public String test(CommandSender sender) {
    return "Only players can run this";
}

@Command("test")
public String test(Player player) {
    ...
}
```

Instead do:

```java
@Command("test")
public String test(CommandSender sender) {
    if (sender instanceof Player) {
        ...
    } else {
        return "Only players can execute this command";
    }
}
```

##### Injected Command Method Parameters
`@Command` methods will have its' parameters automatically injected.
The types of parameters that can be injected are:
* CommandSender (or more specific type) - The sender of the command
* String[] - The arguments of the command
* String -  The command label
* Any other Spring bean TYPE - Ex: Plugin, Server, ...

`@Qualifier` and other parameter annotations of the like are not supported.

##### Dependencies
Do not forget to add the `@PluginDepend` annotation to your project if you require any dependencies.

##### Final Notes
Thanks to https://github.com/Alan-Gomes/mcspring-boot/ for the inspiration of this project!
