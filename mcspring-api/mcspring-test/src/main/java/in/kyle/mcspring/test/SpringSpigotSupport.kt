package `in`.kyle.mcspring.test

import `in`.kyle.mcspring.SpringPlugin
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = [SpringSpigotSupport::class, SpringPlugin::class])
internal open class SpringSpigotSupport
