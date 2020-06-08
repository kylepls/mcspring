mcspring-chat
---
Provides some basic wrappers around the TextComponent API

### Usage

```kotlin
player.sendMessage {
    "hover for details".red() hover {
        ("click to run ".gray()) + ("/test".yellow())
    } command "/test"
}
```

_This may be used independently of mcspring_
