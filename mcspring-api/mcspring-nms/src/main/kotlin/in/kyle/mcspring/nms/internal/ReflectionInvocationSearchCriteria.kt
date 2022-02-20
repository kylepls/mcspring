package `in`.kyle.mcspring.nms.internal

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

data class ReflectionInvocationSearchCriteria(
    private val name: String? = null,
    private val parameterCount: Int? = null,
    private val returnType: Class<*>? = null,
    private val parameterTypes: List<Class<*>>? = null,
    private val static: Boolean? = null
) {

    fun matchSingleMethod(clazz: Class<*>): Method {
        val matchMethods = matchMethods(clazz)
        return matchMethods.singleOrNull()
            ?: error("Did not find exact match for criteria: $this on ${clazz.name}, matches: $matchMethods")
    }

    fun matchSingleConstructor(clazz: Class<*>): Constructor<*> {
        val matchConstructors = matchConstructors(clazz)
        return matchConstructors.singleOrNull()
            ?: error("Did not find exact match for criteria: $this on ${clazz.name}, matches: $matchConstructors")
    }

    fun matchMethods(clazz: Class<*>): List<Method> {
        return clazz.declaredMethods.filter { matchesMethod(clazz, it) }
    }

    fun matchConstructors(clazz: Class<*>): List<Constructor<*>> {
        return clazz.declaredConstructors.filter(::matchesConstructor)
    }

    fun matchesMethod(clazz: Class<*>, method: Method): Boolean {
        return (name == null || method.name == name)
                && (parameterCount == null || method.parameterCount == parameterCount)
                && (returnType == null || typeMatches(returnType, method.returnType))
                && (parameterTypes == null || typeArraysMatch(parameterTypes, method.parameterTypes.toList()))
                && (static == null || Modifier.isStatic(method.modifiers) == static)
                && method.declaringClass == clazz
    }

    fun matchesConstructor(constructor: Constructor<*>): Boolean {
        require(name == null) { "Cannot have named constructor" }
        require(returnType == null) { "Constructor return type implicitly given" }
        return (parameterCount == null || constructor.parameterCount == parameterCount)
                && (parameterTypes == null || typeArraysMatch(parameterTypes, constructor.parameterTypes.toList()))
    }

    private fun typeMatches(proxyType: Class<*>, targetType: Class<*>): Boolean {
        return typeArraysMatch(listOf(proxyType), listOf(targetType))
    }

    private fun typeArraysMatch(proxyTypes: List<Class<*>>, targetTypes: List<Class<*>>): Boolean {
        return proxyTypes.zip(targetTypes)
            .all { (proxyType, targetType) ->
                if (MinecraftReflectionProxyUtils.isReflectionProxyType(proxyType)) {
                    targetType.isAssignableFrom(
                        MinecraftReflectionProxyUtils.unwrapReflectionProxyType(
                            proxyType
                        )
                    )
                } else {
                    true
                }
            }
    }
}
