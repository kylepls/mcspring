package `in`.kyle.mcspring.test

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
        basePackageClasses = [SpringSpigotSupport::class],
        basePackages = ["in.kyle.mcspring"]
)
internal open class SpringSpigotSupport
