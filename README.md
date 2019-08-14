### mc-spring
```java
public class TestPloogin extends SpringPlugin {
    
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

* Cross-plugin injection
* Automatic command registration
* Automatic scheduler registration
* Automatic listener registration (no listener interface needed)
* Injection of common Bukkit objects as beans
  * Plugin
  * Server
  * PluginManager
  * ...
* Optional vault support via (`com.joshb.mcspringboot.economy.EconomyService`)

---
#### Setup

##### Maven

```xml
<repositories>
    <repository>
        <id>spigotSpring</id>
        <url>https://mymavenrepo.com/repo/xY4KhscvfmzoYCoqhY4L/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.joshb.mcspringboot</groupId>
        <artifactId>api</artifactId>
        <version>0.0.1</version>
    </dependency>
    <dependency>
        <groupId>com.joshb.mcspringboot</groupId>
        <artifactId>api-vault</artifactId>
        <version>0.0.1</version>
    </dependency>
    <dependency>
        <groupId>com.joshb.mcspringboot</groupId>
        <artifactId>api-holograms</artifactId>
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
On your main `Plugin` class extend `SpringPlugin` instead of `JavaPlugin` and use the `enable` 
and `disable` methods instead of the `onEnable` and `onDisable` methods.

This will ensure that Spring is able to be properly initialized.

**Do Not**: 
* Use `@Autowired`, especially in the main plugin class. This can lead to 
cyclic dependency issues.
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
