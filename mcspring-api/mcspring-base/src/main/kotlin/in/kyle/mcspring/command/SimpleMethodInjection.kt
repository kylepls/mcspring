package `in`.kyle.mcspring.command

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaType

@Lazy
@Component
class SimpleMethodInjection(
        private val parameterResolvers: List<ParameterResolver>
) {

    fun makeResolvers(contextObjects: List<Any?>): List<ParameterResolver> {
        return contextObjects.filterNotNull().map(this::makeResolverFor)
    }

    private fun makeResolverFor(contextObject: Any): ParameterResolver {
        return object : ParameterResolver {
            override fun resolve(parameter: Class<*>): Any? {
                return contextObject.takeIf { contextObject::class.isSubclassOf(parameter.kotlin) }
            }
        }
    }

    fun callWithInjection(function: KFunction<Any>, types: List<Class<*>>, contextObjects: List<Any?>): Any {
        val contextResolvers = makeResolvers(contextObjects)
        val parameters = getParameters(types, contextResolvers)
        try {
            function.isAccessible = true
        } catch (e: RuntimeException) {
            // java compat required
        }
        return function.call(*parameters)
    }

    fun getParameters(parameters: List<Class<*>>, contextParameterResolvers: List<ParameterResolver>): Array<Any> {
        val methodResolvers = contextParameterResolvers.plus(parameterResolvers).toMutableList()

        return parameters.map { parameter: Class<*> ->
            val candidates = methodResolvers.map { it.resolve(parameter) }.toMutableList()
            val firstIndex = candidates.indexOfFirst { it != null }
            require(firstIndex != -1) { "Unable to resolve parameter $parameter for func($parameters)" }
            methodResolvers.cycleElement(firstIndex)
            candidates[firstIndex]!!
        }.toTypedArray()
    }

    private fun <T> MutableList<T>.cycleElement(index: Int): T {
        val element = removeAt(index)
        add(element)
        return element
    }
}
