package `in`.kyle.mcspring.command

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.reflect

/**
 * Used to inject method parameters
 * Does not support annotated parameters
 */
@Lazy
@Component
class SimpleMethodInjection(
        private val resolvers: List<Resolver>
) {

    fun makeResolvers(vararg contextObjects: Any?): List<Resolver> {
        return contextObjects.filterNotNull().map(this::makeResolverFor)
    }

    private fun makeResolverFor(contextObject: Any): Resolver {
        return object : Resolver {
            override fun invoke(parameter: Class<*>): Any? {
                return if (contextObject::class.java.isAssignableFrom(parameter)) {
                    contextObject
                } else {
                    null
                }
            }
        }
    }

    fun getParameters(func: Function<Any>, contextResolvers: List<Resolver>): Array<Any> {
        val parameters = func.reflect()!!.parameters.map { it.type.javaType as Class<*> }
        return getParameters(parameters, contextResolvers)
    }

    fun getParameters(parameters: List<Class<*>>, contextResolvers: List<Resolver>): Array<Any> {
        val methodResolvers = contextResolvers.plus(resolvers).toMutableList()

        return parameters.map { parameter: Class<*> ->
            val candidates = methodResolvers.map { it(parameter) }.toMutableList()
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
