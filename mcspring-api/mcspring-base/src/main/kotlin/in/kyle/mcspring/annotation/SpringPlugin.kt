package `in`.kyle.mcspring.annotation

import `in`.kyle.mcspring.SpringSpigotSupport
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@SpringBootApplication
@Import(SpringSpigotSupport::class)
@EnableAutoConfiguration
annotation class SpringPlugin
