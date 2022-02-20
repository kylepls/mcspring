package `in`.kyle.mcspring.nms.internal

import `in`.kyle.mcspring.nms.internal.MinecraftPackages.replacePackagesWithPrefix
import java.lang.reflect.Method

internal object FriendlyStrings {
    fun getMethodString(method: Method): String {
        val returnType = replacePackagesWithPrefix(method.returnType.canonicalName)
        val methodName = "${replacePackagesWithPrefix(method.declaringClass.canonicalName)}.${method.name}"

        val args = method.parameterTypes
            .joinToString(", ") { replacePackagesWithPrefix(it.canonicalName) }
            .let { "($it)" }

        return "$returnType $methodName$args"
    }
}
