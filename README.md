### mc-spring
```java
public class TestPloogin { // We don't have to extend JavaPlugin
    
    @Command("test")
    public String playerSender(Player sender, String command) {
        return command + " works!";
    }
    
    @Scheduled(fixedDelay = 10000)
    public void interval() {
        Bukkit.broadcastMessage("REMEMBER TO DONATE");
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        getLogger().info(e.getPlayer().getName() + " moved");
    }
}
```

---

#### Features
_(Spring beans only)_

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

---
#### Maven Setup

I tried to make this as easy as possible. If you're using Intellij just do the following.

1. Create a new project
2. In the create dialog select `Maven -> create from archetype`
3. Click `Add archetype` on the right-hand side
4. Type in the following:

    1. group: `in.kyle.mcspring` 
    2. artifact: `archetype` 
    3. version: `0.0.2` 
    4. ur: `https://mymavenrepo.com/repo/SmnHSudeBfo1zzCti47R/` 

Then a project will be created for you with a project-specific Spigot folder already setup for you.

Next:

1. Run a Maven install to create the required files
2. Create a new JAR run configuration
3. Select the downloaded spigot.jar as the target
4. Change the run environment to the spigot folder
5. Add the following VM flag `-DIReallyKnowWhatIAmDoingISwear`
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

Injection functionality can be extended by creating an `Resolver` component:

```java
@Component
class IntResolver implements Resolver {
    public Optional<Object> resolve(Parameter parameter) {
        if (parameter.getType().equals(int.class)) {
            return Optional.of(69);
        } else {
            return Optional.empty();
        }
    }
}
```

##### Dependencies
Do not forget to add the `dependencies` tag to your plugin.yml if you require any dependencies.

##### Final Notes
Thanks to https://github.com/Alan-Gomes/mcspring-boot/ for the inspiration/direction of this 
project!
