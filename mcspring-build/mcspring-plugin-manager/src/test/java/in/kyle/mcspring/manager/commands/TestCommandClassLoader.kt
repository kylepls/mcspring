package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
internal class TestCommandClassLoader {

    @Autowired
    lateinit var commandExecutor: TestCommandExecutor

    @Test
    fun testClassLoader() {
        val output: List<String> = commandExecutor.run("classloader " + javaClass.name)
        assertThat(output).hasSize(2)
        assertThat(output[0]).isEqualTo("ClassLoader: ${javaClass.classLoader}")
        assertThat(output[1]).isEqualTo("Domain: ${javaClass.protectionDomain.codeSource.location}")
    }
}
