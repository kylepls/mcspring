This is a simple Spigot installation.

To start the server in Intellij:
1. Go to `Edit Run Configurations` (next to the run button in the dropdown)
2. Add a new JAR run configuration by clicking the plus button and selecting `JAR Application`
3. Set the `Path to JAR` as the downloaded spigot.jar. If you do not have a spigot.jar, run a maven install.
4. Add the VM Option `-DIReallyKnowWhatIAmDoingISwear`
5. Set the `Working Directory` as the spigot folder

You will then be able to run spigot by clicking the run button in IJ.

Further Notes:
* You are also able to launch Spigot in debug mode and set breakpoints.
* Running a maven install will automatically copy the latest plugin jar into your plugins folder.
* Once you perform a new install, restart the server to get the latest version. McSpring does not
 yet support reloading altered jars.
