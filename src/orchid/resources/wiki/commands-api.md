## The Basics
Commands can be a pain in the ass to write. We tried our best to mitigate most of that pain.

Annotating any method with `@Command(String)` inside a Spring bean will automatically register that command upon the plugin loading. If the method returns a value, that value will be converted to a string and sent to the CommandSender. There are also three command-specific injections: 
* `Player` - The player that sent the command
* `String` - The label of the command
* `String[]` - The args array of the command

These can be injected in any order or not injected at all if you don't need them.

Here is an example command, we'll walk through what it actually does:

```java
@Command("test-command")
public String test(String label, Plugin plugin, Player player) {
    return String.format("The command label was '%s'\nThe plugin was '%s'\nThe sender was '%s'", label, plugin.getName(), player.getName();
}
```

This command is run by typing `/test-command` in the console. `String label` corresponds to the label of the command. In this case, it's just `test-command`. `Plugin plugin` corresponds to the plugin the command is registered to. `Plugin` is defined as a Spring bean by mcspring so it can be automatically injected in this method. `Player player` corresponds to the player that sent the command (e.g.: the sender).

The return type of this command is a String. So the output of this method is then sent to the command sender. 

_It should also be noted that this method may be public/package-protected/protected depending on your preference. It doesn't matter._

## Things To Watch Out For
There are some gimmicks to writing these commands. We're trying our best to get these polished out.

### Do not duplicate `@Command` annotations

Consider the following example:

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

This will register just 1 command method.

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

### The command injection only injects typed beans

Beans that are referenced by `@Qualifier` and the like are not distinguished. Therefore you should avoid using named beans with command-method injection.

**NON-WORKING EXAMPLE**
```java
@Command("test")
public String test(@Qualifier("my-bean-1") Api api1, @Qualifier("my-bean-2") Api api2) {
    // api1 and api2 will not be injected properly
}
```
