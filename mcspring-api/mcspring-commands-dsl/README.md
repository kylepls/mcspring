mcspring-commands-dsl
---
_My hot take on command parsing, completely optional._

### Project Goals
One of the worst parts of writing plugins is writing command parsers.

One of the primary problems when writing parsers as a conditional tree is the tendency of depth
 spiraling out of control. What could be a simple command could turn into a nested cataclysm of conditional branching.

Here's some real code that I wrote not too long ago. The problems here are not worth listing out.
```kotlin
if (party.hasAdminPermissions(player)) {
    if (args.size == 2) { // party size <value>
        val size = args[1].toIntOrNull()
        if (size != null) {
            if (size <= 12) {
                if (size >= party.members.size) {
                    party.size = size
                    showPartyInfo(player, party)
                } else {
                    player.sendMessage("Your party has too many members for that size. You must kick some players first. ($size < ${party.members.size}")
                }
            } else {
                player.sendMessage("Max party size is 12")
            }
        } else {
            player.sendMessage("Invalid party size: ${args[1]}")
        }
    } else {
        player.sendMessage("Usage: /$label size <size>")
    }
} else {
    player.sendMessage("You must be a party admin to do this")
}
```

Flattening this mess is easy with some non-standard control flow. Next is to clean up the
 redundant mess of if-return blocks. I started by commenting the type of task being performed by
  each block. This will be useful later.

```kotlin
// Condition
if (!party.hasAdminPermissions(player)) {
    player.sendMessage("You must be a party admin to do this")
    return
}

// Usage message
if (args.size != 2) { // party size <value>
    player.sendMessage("Usage: /$label size <size>")
    return
}

// Argument parser
val size = args[1].toIntOrNull()
if (size == null) {
    player.sendMessage("Invalid party size: ${args[1]}")
    return
}

// Argument condition
if (size > 12) { // TODO config
    player.sendMessage("Max party size is 12")
    return
}

// Argument condition
if (size < party.members.size) {
    player.sendMessage("Your party has too many members for that size. You must kick some players first. ($size < ${party.members.size}")
    return
}

// Command execution
party.size = size
showPartyInfo(player, party)
```

These statements are often highly repetitive in nature. Abstracting out the general concepts to a
 DSL is more-or-less straightforward:

```kotlin
require(party.hasAdminPermissions(sender as Player)) {
    // execution
    message("You must be a party admin to do this")
    // Dead end
}

val size by intArg {
    parser {
        lessThanEqual(12) {
            // execution
            message("Invalid party size ${args[1]}")
            // Dead end
        }
        lessThanEqual(party.members.size) {
            // execution
            message("Your party has too many members for that size. You must kick some players first. (${args[1]} < ${party.members.size}")
            // Dead end
        }
    }
}

then {
    // execution
    party.size = size
    showPartyInfo(player, party) 
}
```

This approach has some neat advantages over a traditional command structure. First, there is a
 clear structure to how the command is parsed. Second, this format natively lends itself to
  automatic tab completion. That means that any command written in this DSL format is natively
   tab-completable.
   
Tab completions are handled by running the command DSL but ignoring execution sections (see
 comments). 
