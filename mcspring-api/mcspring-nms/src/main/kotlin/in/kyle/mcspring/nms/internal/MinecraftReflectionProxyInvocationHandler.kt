package `in`.kyle.mcspring.nms.internal

import `in`.kyle.mcspring.nms.ObfuscatedName
import `in`.kyle.mcspring.nms.StaticMethod
import `in`.kyle.mcspring.nms.UnwrapMethod
import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils.isReflectionProxyType
import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils.tryUnwrapProxyArgs
import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils.tryUnwrapProxyClass
import `in`.kyle.mcspring.nms.internal.MinecraftReflectionProxyUtils.unwrapReflectionProxyType
import java.lang.invoke.MethodHandles
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

internal class MinecraftReflectionProxyInvocationHandler<T : Any>(
    val instance: Any,
    private val interfaceClass: Class<T>
) : InvocationHandler {

    init {
        require(!MinecraftReflectionProxyUtils.isReflectionProxyInstance(instance)) { "Cannot create nested proxy types" }
    }

    override fun invoke(proxy: Any, proxyMethod: Method, args: Array<out Any?>?): Any? {
        return when {
            proxyMethod.isDefault -> invokeDefaultMethod(proxy, proxyMethod, args)
            proxyMethod.isAnnotationPresent(UnwrapMethod::class.java) -> instance
            else -> callMethodOrProperty(proxyMethod, args)
        }
    }

    private fun callMethodOrProperty(proxyMethod: Method, args: Array<out Any?>?): Any? {
        val kotlinProperty = findKotlinProperty(proxyMethod)
        return if (kotlinProperty != null) {
            val clazz = unwrapReflectionProxyType(interfaceClass)
            val fieldType = tryUnwrapProxyClass(kotlinProperty.javaGetter!!.returnType)
            val properties =
                interfaceClass.kotlin.declaredMemberProperties.filter {
                    unwrapReflectionProxyType(it.javaGetter!!.returnType) == fieldType
                }
            val index = properties.indexOf(kotlinProperty)
            require(index >= 0) { "Could not find property $kotlinProperty on kotlin class ${interfaceClass.kotlin}" }
            val field =
                clazz.declaredFields.filter { it.type == fieldType }.elementAtOrNull(index)
                    ?: error(
                        """
                        ${index + 1} field(s) of type $fieldType not found on class $clazz
                        was expecting at least ${index + 1}
                    """.trimIndent()
                    )

            field.isAccessible = true
            if (proxyMethod.name.startsWith("get")) {
                val result = field[instance]
                return if (isReflectionProxyType(proxyMethod.returnType)) {
                    MinecraftReflectionProxyUtils.createProxy(result, proxyMethod.returnType)
                } else {
                    result
                }
            } else {
                field[instance] = args!![0]
            }
        } else {
            callRealMethod(proxyMethod, args)
        }
    }

    private fun findKotlinProperty(proxyMethod: Method): KProperty1<T, *>? {
        return if (proxyMethod.name.length > 3) {
            interfaceClass.kotlin.memberProperties.singleOrNull {
                val propertyName = proxyMethod.name.substring(3)
                it.name.equals(propertyName, ignoreCase = true)
            }
        } else {
            null
        }
    }

    private fun callRealMethod(proxyMethod: Method, args: Array<out Any?>?): Any? {
        val actualMethod = lookupRealMethod(proxyMethod)
        val mappedArgs = tryUnwrapProxyArgs(args)

        return try {
            val result = actualMethod.invoke(instance, *mappedArgs)
            if (isReflectionProxyType(proxyMethod.returnType)) {
                MinecraftReflectionProxyUtils.createProxy(result, proxyMethod.returnType)
            } else {
                result
            }
        } catch (e: Exception) {
            val methodString = FriendlyStrings.getMethodString(actualMethod)
            val argTypes = mappedArgs.map { it?.javaClass }
            throw InvocationTargetException(e, "Could not invoke $methodString with $argTypes")
        }
    }

    private fun lookupRealMethod(proxyMethod: Method): Method {
        val isIgnoreName = proxyMethod.isAnnotationPresent(ObfuscatedName::class.java)
        val isStatic = proxyMethod.isAnnotationPresent(StaticMethod::class.java)

        val searchCriteria = ReflectionInvocationSearchCriteria(
            proxyMethod.name.takeUnless { isIgnoreName },
            proxyMethod.parameterCount,
            proxyMethod.returnType,
            proxyMethod.parameterTypes.toList(),
            isStatic
        )

        val clazz = unwrapReflectionProxyType(interfaceClass)
        return searchCriteria.matchSingleMethod(clazz).apply { isAccessible = true }
    }

    private fun invokeDefaultMethod(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        method.isAccessible = true
        return MethodHandles.privateLookupIn(interfaceClass, MethodHandles.lookup())
            .`in`(interfaceClass)
            .unreflectSpecial(method, interfaceClass)
            .bindTo(proxy)
            .invokeWithArguments(args?.toList() ?: emptyList<Any?>())
    }

    override fun toString() = "MinecraftReflection[$interfaceClass: $instance]"
}
