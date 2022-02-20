package `in`.kyle.mcspring.nms.internal

import `in`.kyle.mcspring.nms.ForClass
import java.lang.reflect.Proxy

object MinecraftReflectionProxyUtils {

    fun <T : Any> createProxy(obj: Any, clazz: Class<T>): T {
        val classLoader = object : Any() {}.javaClass.classLoader
        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(
            classLoader,
            arrayOf(clazz),
            MinecraftReflectionProxyInvocationHandler(obj, clazz)
        ) as T
    }

    fun isReflectionProxyInstance(obj: Any) = try {
        Proxy.getInvocationHandler(obj) is MinecraftReflectionProxyInvocationHandler<*>
    } catch (_: IllegalArgumentException) {
        false
    }

    fun isReflectionProxyType(type: Class<*>) = type.isAnnotationPresent(ForClass::class.java) && type.isInterface

    fun unwrapReflectionProxyType(proxyType: Class<*>): Class<*> {
        val forClassAnnotation = proxyType.getAnnotation(ForClass::class.java)
            ?: error("ForClass annotation missing on ${proxyType.canonicalName}")
        val className = forClassAnnotation.name
        return if (className.isNotEmpty()) {
            MinecraftPackages.getClass(className)
        } else {
            forClassAnnotation.clazz.java
        }
    }

    fun unwrapReflectionProxyInstance(proxy: Any): Any {
        val invocationHandler = Proxy.getInvocationHandler(proxy)
        return if (invocationHandler is MinecraftReflectionProxyInvocationHandler<*>) {
            invocationHandler.instance
        } else {
            proxy
        }
    }

    fun tryUnwrapProxyArgs(args: Array<out Any?>?): Array<Any?> {
        return args?.map(MinecraftReflectionProxyUtils::tryUnwrapProxyArg)
            ?.toTypedArray()
            ?: emptyArray()
    }

    private fun tryUnwrapProxyArg(arg: Any?): Any? {
        if (arg != null) {
            if (isReflectionProxyInstance(arg)) {
                return unwrapReflectionProxyInstance(arg)
            }
        }
        return arg
    }

    fun tryUnwrapProxyClasses(classes: List<Class<*>>): List<Class<*>> {
        return classes.map(this::tryUnwrapProxyClass)
    }

    fun tryUnwrapProxyClass(clazz: Class<*>): Class<*> {
        return if (isReflectionProxyType(clazz)) {
            unwrapReflectionProxyType(clazz)
        } else {
            clazz
        }
    }
}
