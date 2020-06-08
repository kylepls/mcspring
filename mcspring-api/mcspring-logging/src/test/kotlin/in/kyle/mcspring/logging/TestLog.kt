package `in`.kyle.mcspring.logging

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(LogCallAspect::class)
@ComponentScan(basePackageClasses = [LogCall::class])
open class TestLog {

    @Autowired
    lateinit var test: TestComponent

    @Autowired
    lateinit var ctx: ApplicationContext

    @Test
    fun test() {
        test.test()
        LoggerFactory.getLogger("mcspring").info("wtf")
    }

    @SpringBootConfiguration
    open class Config {
    }
}
