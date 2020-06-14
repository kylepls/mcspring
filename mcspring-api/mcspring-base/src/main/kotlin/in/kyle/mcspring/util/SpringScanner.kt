package `in`.kyle.mcspring.util

import org.springframework.aop.support.AopUtils
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class SpringScanner(private val context: ApplicationContext) {

    fun scanMethods(vararg annotations: Class<out Annotation>): Map<Method, Any> {
        val methods = mutableMapOf<Method, Any>()
        for (beanName in context.beanDefinitionNames) {
            val obj = context.getBean(beanName)
            for (method in getRealClass(obj).declaredMethods) {
                if (annotations.any { method.isAnnotationPresent(it) }) {
                    methods[method] = obj
                }
            }
        }
        return methods
    }

    private fun getRealClass(obj: Any): Class<*> {
        return if (AopUtils.isAopProxy(obj.javaClass)) {
            AopUtils.getTargetClass(obj)
        } else {
            obj.javaClass
        }
    }
}
