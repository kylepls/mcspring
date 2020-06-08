package `in`.kyle.mcspring.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.core.io.support.PathMatchingResourcePatternResolver


@Configuration
@EnableAspectJAutoProxy
open class LoggingConfiguration {
    @Bean("mcspring-logger")
    open fun logger(): Logger {
        registerConfigurations()
        return LoggerFactory.getLogger("mcspring")
    }

    private fun registerConfigurations() {
        val loader = PathMatchingResourcePatternResolver()
        val resources = loader.getResources("classpath*:log4j2-*.xml")
        System.setProperty("log4j.configurationFile", resources.joinToString(separator = ",") { it.filename!! })
    }
}
