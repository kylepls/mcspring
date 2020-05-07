package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
internal class TestCommandAbout {

    @Autowired
    lateinit var commandExecutor: TestCommandExecutor

    @Test
    fun testAbout() {
        val output: List<String> = commandExecutor.run("about")
        assertThat(output).hasSize(4)
        assertThat(output[0]).matches("Plugin Name: [^ ]+")
        assertThat(output[1]).matches("Plugin Version: [^ ]+")
        assertThat(output[2]).matches("Spring Version: [^ ]+")
        assertThat(output[3]).matches("Bukkit Version: [^ ]+")
    }
}
