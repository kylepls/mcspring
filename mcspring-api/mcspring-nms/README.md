mcspring-guis
---
Provides a straightforward NMS API.

Here's the idea, we define an interface for the reflective access we need. Then we can use the interface as a proxy object to interact with the objects at runtime.

### To use...
1. First define the object you want reflective access to, in this instance the OBC world. 
2. Add a method that matches the method signature in the OBC class. This method can have another _accessor_ class in-place of the OBC/NMS class.

```kotlin
@ForClass("{obc}.CraftWorld") // the path to the class, {obc} and {nms} are filled in at runtime
interface CraftWorldAccessor {

    fun save(any: Any): Any

    fun addEntity(entity: NmsEntityAccessor, reason: CreatureSpawnEvent.SpawnReason): Entity

    fun addEntity(entity: CraftEntityAccessor, reason: CreatureSpawnEvent.SpawnReason) = addEntity(entity.getHandle(), reason)

    fun createEntity(location: Location, entityClass: Class<out Entity>): NmsEntity

}

@ForClass("{obc}.entity.CraftEntity")
interface CraftEntity {

    fun save(): NbtTagCompound

    fun getNbtString() = save().toString()

    fun getHandle(): NmsEntity

    @StaticMethod
    fun getEntity(server: Server, entity: NmsEntity): Entity

    @StaticMethod
    fun getEntity(nmsEntity: NmsEntity): Entity = getEntity(Bukkit.getServer(), nmsEntity)

    @UnwrapMethod
    fun unwrap(): Entity

}

// helper method, not needed
val World.craftWorld
    get() = this.obc<CraftWorld>()
```

Then you just have to call the `obc/nms` or `obcStatic/nmsStatic` methods to obtain an instance of the accessor.

```kotlin
    fun <T : Entity> makeEntityNms(
        entityType: Class<T>,
        bukkitWorld: World,
        nbt: NbtTagCompound? = null
    ): T {
        val craftWorld = bukkitWorld.obc<CraftWorld>() // get the OBC world
        val nmsEntity = craftWorld.createEntity(bukkitWorld.spawnLocation, entityType) // call the createEntity method on the accessor object - calls the OBC method, returns an accessor object
        if (nbt != null) {
            nmsEntity.load(nbt)
        }
        val bukkitEntity = obcStatic<CraftEntityAccessor>().getEntity(nmsEntity) // obtains an accessor for calling static class methods
        return bukkitEntity as T
    }
```
