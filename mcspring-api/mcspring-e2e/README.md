mcspring-e2e
---
This is my attempt to create E2E testing for plugins. It's not complete, but it's a cool concept. 
Maybe you should create a PR?

### Examples

This isn't complete yet, but the idea is the following:
1. Instrument a JUnit test to start a Spigot instance
```kotlin
class MyTest : SpigotServerTest {
    @Test
    fun test() {
        // wait for Spigot to start
        // attach and run some code as a plugin
        // verify the behavior of your existing plugin
        // kill Spigot
    }
}
```

_This may be used independently of mcspring_
