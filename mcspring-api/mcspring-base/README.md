mcspring-base

---

_A lightweight Spring Boot wrapper for Bukkit_

This is the "core" of mcspring. Most of the magic sauce is in here (and the gradle plugin).

* Adds support for `@Scheduled`, `@EventHandler` and plugin dependency annotations. 
* Bootstraps Spring on top of the plugin system.

### Quickstart

#### Annotations:
* `@PluginDepend` - Add this annotation to a Spring component to add plugin dependencies to the generated `plugin.yml`
* `@SoftPluginDepend` - Same as above but for soft dependencies.

#### Events:
Register a class as a component and event handlers will be automatically registered.

```kotlin
@Component
class Demo {
    @EventHandler
    fun move(e: PlayerMoveEvent) {
        println(e.getPlayer().getName().toString() + " moved")
    }
}
```

#### Scheduling
There are 2 ways to schedule things depending on your use case.

#1: Global Scheduling Annotation: See [The @Scheduled Annotation in Spring](https://www.baeldung.com/spring-scheduled-tasks)

#2: A convenience wrapper for the Bukkit scheduler. See: in.kyle.mcspring.scheduler.SchedulerService.
