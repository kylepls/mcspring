package `in`.kyle.mcspring.logging

import org.apache.logging.log4j.LogManager
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Service

@Aspect
@Service
class LogCallAspect {

    @Before("@within(in.kyle.mcspring.logging.LogCall) || @annotation(in.kyle.mcspring.logging.LogCall)")
    fun logCall(joinPoint: JoinPoint) {
        println("swag")
        val sig = joinPoint.signature as MethodSignature
        val annotation = sig.method.getAnnotation(LogCall::class.java)
        val logger = LogManager.getLogger(annotation.logger)
        val parameters = if (annotation.logParameters) {
            joinPoint.args.zip(sig.method.parameters).joinToString(", ") { "${it.second.name}=${it.first}" }
        } else {
            ""
        }
        logger.trace("Call ${sig.method.name} $parameters")
        println("swag")
        println("swag")
        println("swag")
        println("swag")
    }
}
