mcspring-chat-actions
---
Simplifies the process of adding interactable elements to chat messages.

### Examples

```kotlin
player.sendMessage {
    "hover for details".red() hover {
        ("click to run ".gray()) + ("/test".yellow())
    } command "/test"
}
```

_This may be used independently of mcspring_
