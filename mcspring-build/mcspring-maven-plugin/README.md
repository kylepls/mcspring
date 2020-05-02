##MC-Spring Auto Generation Plugin for Maven

###What does this do?
This plugin removes the need to create a plugin.yml and main class.
At compile time we resolve all dependencies that your plugin uses.
Those dependencies will be inserted into the plugin.yml, along with
various maven properties that you can define in the pom.xml.

###How does it work?
Step 1: Resolve all maven project dependencies and find the corresponding
jars associated with them to add to a class loader. Load all classes
from the jars and check for the @PluginDepend annotation provided by MC-Spring.

Step 2: Profit