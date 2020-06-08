Long story short, don't make anything extend JavaPlugin and don't create a plugin.yml.

---

### Extending JavaPlugin
You don't need a main class nor do you need to extend JavaPlugin. I know what you're thinking, "what about onEnable/onDisable?" We have you covered.
Instead of using the `onEnable` and `onDisable` methods use the `@PostConstract` and `@PreDestroy` annotations on any method anywhere in the project. You can make as many of these methods as you like. I suggest you create one on every Spring bean that needs something setup (e.g.: connecting to Mongo/MySQL).

### The plugin.yml
Don't bother with creating `plugin.yml` either, mcspring will take care of that. 

If you need to add a dependency to the `plugin.yml` instead use the `@PluginDepends` annotation somewhere in your project and it will be automatically added for you. 

If you need to set the plugin version, name, or description add the `@SpringPlugin` annotation to a class somewhere inside your plugin.
