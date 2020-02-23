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

* No main plugin class required
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
#### Setup

##### Maven

```xml
<repositories>
    <repository>
        <id>mcspring</id>
        <url>https://kyle.in/maven</url>
    </repository>
</repositories>

<dependencies>
    <!--If you're using Lombok, make sure to put that first-->
    <dependency>
        <groupId>in.kyle.mcspring</groupId>
        <artifactId>mcspring</artifactId>
        <version>0.0.1</version>
    </dependency>
    <dependency>
        <groupId>in.kyle.mcspring</groupId>
        <artifactId>mcspring-vault</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>
```

##### Shading
Make sure to shade in the dependency
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.2.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

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
