I tried to make this as easy as possible. If you're using IntelliJ just do the following.

1. Create a new project
2. In the create dialog select `Maven -> create from archetype`
3. Click `Add archetype` on the right-hand side
4. Type in the following:

    1. group: `in.kyle.mcspring` 
    2. artifact: `mcspring-starter` 
    3. version: `0.0.7` <-- Change this to the latest release over [here](https://github.com/kylepls/mcspring/releases) if I forget to update it.

Then a project will be created for you with a project-specific Spigot folder already set up for you.

Next:

1. Run a Maven install to create the required files
2. Create a new JAR run configuration
3. Select the downloaded `spigot.jar` in the `spigot` folder as the target
4. Change the run environment to the `spigot` folder
5. Add the following **VM flag** `-DIReallyKnowWhatIAmDoingISwear`
6. Add the following **Program Argument** `--nogui`

![](https://i.imgur.com/waoHVTz.png)

7. To update the plugin on the server, just run a maven install and restart the server.

---

Now that you're all set up, head over [here](https://github.com/kylepls/mcspring/wiki/The-Commands-API) and read about how to define commands in mcspring. Also, head over [here](https://github.com/kylepls/mcspring/wiki/The-plugin.yml-and-Extending-JavaPlugin) to read about what to do now that you don't have a main class or plugin.yml.
