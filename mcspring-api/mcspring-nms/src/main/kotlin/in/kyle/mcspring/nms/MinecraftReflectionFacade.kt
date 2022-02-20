package `in`.kyle.mcspring.nms

import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils
import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils.tryUnwrapProxyArgs
import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils.unwrapReflectionProxyType
import `in`.kyle.mcspring.nms.internal.ReflectionInvocationSearchCriteria

inline fun <reified T : Any> Any.obc(): T = this.nms()
inline fun <reified T : Any> Any.nms(): T = MinecraftReflectionProxyUtils.createProxy(this, T::class.java)

object StaticObject

inline fun <reified T : Any> obcStatic(): T = StaticObject.obc()
inline fun <reified T : Any> nmsStatic(): T = StaticObject.nms()

inline fun <reified T : Any> obcConstructor(vararg args: Any) = nmsConstructor<T>(args)
inline fun <reified T : Any> nmsConstructor(vararg args: Any): T {
    val type = unwrapReflectionProxyType(T::class.java)

    val passArgs = tryUnwrapProxyArgs(args).requireNoNulls()

    val constructor = ReflectionInvocationSearchCriteria(
        parameterCount = args.size,
        parameterTypes = passArgs.map { it.javaClass }
    ).matchSingleConstructor(type)

    return constructor.newInstance(*passArgs).nms()
}
