## Getting Setup
Writing sub-commands is #1 on my list of things that are tedious and annoying. How about we change that?

mcspring has an optional dependency called `mcspring-subcommands`. If it is not already; you can add it to your project with the following dependency. **MAKE SURE TO REPLACE THE VERSION WITH YOUR MCSPRING VERSION**

```xml
<dependency>
    <groupId>in.kyle.mcspring</groupId>
    <artifactId>mcspring-subcommands</artifactId>
    <version>REPLACE WITH X.X.X</version>
</dependency>
```

Now we're ready to begin.

## The Basics

Sub-commands are defined in two parts. Firstly, there is the structure of the command. This defines how the command is read in and performs validation on the input. Secondly, there is the executor. This takes in the parsed values and does something on the server. If it's not clear, don't worry, just keep reading and the examples will do the talking.

For this example, we will make a plot command to manage a plot plugin. This will look something like the following:
* `/plot <create|add|tp>`
* `/plot create`
* `/plot add <player>`
* `/plot tp <x> <y>`

### The root method
Firstly, you will start with an `@Command` annotated method. This will have a return type of `void` and a parameter of type `PluginCommand`.

```java
@Command("plot")
void plot(PluginCommand command) {
}
```

Now we want to define our first three sub-commands. These are the `<create|add|tp>` parts. A sub-command can be thought of as its own command in mcspring. We will then string up these individual commands to compose the full command. 

The `PluginCommand#on(String, Consumer<PluginCommand>)` method allows the creation of these sub-commands. For a consumer, I suggest you define a new method in the same class to keep things organized.

```java
@Command("plot")
void plot(PluginCommand command) {
    command.on("create", this::plotCreate);
    command.on("add", this::plotAdd);
    command.on("tp", this::plotTp);
}

void plotCreate(PluginCommand command) {
}

void plotAdd(PluginCommand command) {
}

void plotTp(PluginCommand command) {
}
```

Now we treat `plotCreate` as its own command and start over. There is no additional parsing to be done so we can now create that plot for the user. If permissions checking, database lookups, etc.. have to be done; it should be in the execute method that we will soon define.

The `PluginCommand#then(...)` method allows you to inject all parsed parameters into another method an run it. This is where your "work" should be done.

```java
@Command("plot")
void plot(PluginCommand command) {
    command.on("create", this::plotCreate);
    command.on("add", this::plotAdd);
    command.on("tp", this::plotTp);
}

void plotCreate(PluginCommand command) {
    command.then(this::plotCreateExecutor);
}

void plotAdd(PluginCommand command) {
    command.then(this::plotAddExecutor);
}

void plotTp(PluginCommand command) {
    command.then(this::plotTpExecutor);
}

void plotCreateExecutor(Player sender) {
    // create the plot for the sender
}

void plotAddExecutor(Player sender) {
    // add the player to the plot
}

void plotTpExecutor(Player sender) {
    // tp the player to the plot
}
```

Now we need to parse out the actual information in the commands. The `with...` methods allow you to parse individual arguments of the command and inject them into the executor method. These `with` calls should be put before the `execute` call. The `with...` parameters are injected into the method before anything else **IN ORDER**.

```java
@Command("plot")
void plot(PluginCommand command) {
    command.on("create", this::plotCreate);
    command.on("add", this::plotAdd);
    command.on("tp", this::plotTp);
}

void plotCreate(PluginCommand command) {
    command.then(this::plotCreateExecutor);
}

void plotAdd(PluginCommand command) {
    command.withPlayer("Argument was not a player");
    command.then(this::plotAddExecutor);
}

void plotTp(PluginCommand command) {
    command.withInt("Argument was not an integer"); // parse x
    command.withInt("Argument was not an integer"); // parse y
    command.then(this::plotTpExecutor);
}

void plotCreateExecutor(Player sender) {
    // create the plot for the sender
}

void plotAddExecutor(Player target, Player sender) {
    // add the player to the plot
}
```

### Some final touches
We actually are already done. Just a few more things to make it better.

Here are a few issues:
* In the event a player runs `/plot asdflksjadf` nothing will happen. 
* In the event a player runs `/plot` nothing will happen. 
* In the event a player runs `/plot add` nothing will happen. 
* In the event a player runs `/plot tp` nothing will happen.

We need to add fallbacks for these cases.

For an invalid sub-command being passed; we use `onInvalid`. The `Function` argument is called with the players sub-command if no valid sub-command was yet called. This should be called after all the `on` and `with` methods.

For no sub-command being passed; we use `otherwise`. As the name notes, this runs if nothing yet has happened. It is the ultimate fallback. This should be called **LAST**.

```java
@Command("plot")
void plot(PluginCommand command) {
    command.on("create", this::plotCreate);
    command.on("add", this::plotAdd);
    command.on("tp", this::plotTp);
    command.onInvalid(s -> String.format("%s is not a valid sub-command", s));
    command.otherwise("Usage: /plot <create|add|tp>");
}

void plotCreate(PluginCommand command) {
    command.then(this::plotCreateExecutor);
}

void plotAdd(PluginCommand command) {
    command.withPlayer("Argument was not a player");
    command.then(this::plotAddExecutor);
    command.otherwise("Usage: /plot add <player>");
}

void plotTp(PluginCommand command) {
    command.withInt("Argument was not an integer"); // parse x
    command.withInt("Argument was not an integer"); // parse y
    command.then(this::plotTpExecutor);
    command.otherwise("Usage: /plot tp <x> <y>");
}

void plotCreateExecutor(Player sender) {
    // create the plot for the sender
}

void plotAddExecutor(Player target, Player sender) {
    // add the player to the plot
}

void plotTpExecutor(Player sender, int x, int y) {
    // tp the player to the plot
}
```

## But Wait... There's More.

Good job on making it this far. You've surely read a lot already so I'll make this last part short.

Tab completion is automatically calculated for every command using the sub-command API. This is done by calling the command method with a dummy object that records the valid options for a command and then sending that to the player. That is why it is **SUPER IMPORTANT** that you **NEVER** put expensive command logic or anything other than `PluginCommand` calls inside the parsing methods.

---

Holy smokes, you read all of that? Well if you have not yet read enough head over [here](https://github.com/kylepls/mcspring/wiki/The-Sub-Commands-API) to learn how to create sub-commands with tab completions.
