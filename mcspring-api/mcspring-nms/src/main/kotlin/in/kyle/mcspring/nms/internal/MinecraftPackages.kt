package `in`.kyle.mcspring.nms.internal

import org.bukkit.Bukkit

internal object MinecraftPackages {

    val OBC_PREFIX = Bukkit.getServer().javaClass.getPackage().name
    val NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server")
    val VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "")

    private val replaceVariables = mapOf(
        "{obc}" to OBC_PREFIX,
        "{nms}" to NMS_PREFIX,
        "{version}" to VERSION
    )

    private val classCache = mutableMapOf<String, Class<*>>()

    fun getClass(nameWithVariables: String): Class<*> {
        return classCache.getOrPut(nameWithVariables) {
            val canonicalClass = replaceVariables.toList()
                .fold(nameWithVariables) { acc, (variable, replacement) ->
                    acc.replace(variable, replacement)
                }
            Class.forName(canonicalClass)
        }
    }

    fun replacePackagesWithPrefix(string: String): String {
        return replaceVariables.toList()
            .associate { (key, value) -> value to key }
            .toList()
            .fold(string) { acc, (variable, replacement) ->
                acc.replace(variable, replacement)
            }
    }
}
