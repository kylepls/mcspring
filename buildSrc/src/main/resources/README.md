mcspring server
---

1. Run the `buildServer` gradle task to generate the required files. 
This will recompile the plugin jar and copy it over for you.
2. Create a new JAR run configuration in Intellij
3. Select the downloaded `spigot.jar` in the `spigot` folder as the target
4. Change the run environment to the `spigot` folder
5. Add the following **VM flag** `-DIReallyKnowWhatIAmDoingISwear`
6. Add the following **Program Argument** `--nogui`

![](https://i.imgur.com/waoHVTz.png)

7. To update the plugin on the server, run the `buildServer` task and restart the server.
