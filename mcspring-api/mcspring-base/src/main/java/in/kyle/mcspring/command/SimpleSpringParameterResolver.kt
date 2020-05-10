package `in`.kyle.mcspring.command

import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
internal class SimpleSpringParameterResolver(
        private val context: ApplicationContext
) : ParameterResolver {
    override fun resolve(parameter: Class<*>): Any? {
        return try {
            context.getBean(parameter)
        } catch (e: NoSuchBeanDefinitionException) {
            null
        }
    }
}
