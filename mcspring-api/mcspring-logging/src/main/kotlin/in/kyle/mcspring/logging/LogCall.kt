package `in`.kyle.mcspring.logging

import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class LogCall(
    val logger: String = "mcspring",
    val logParameters: Boolean = false
)

