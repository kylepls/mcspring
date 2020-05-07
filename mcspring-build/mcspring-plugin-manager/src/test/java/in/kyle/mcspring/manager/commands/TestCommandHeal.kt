package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
internal class TestCommandHeal {

    @Autowired
    lateinit var commandExecutor: TestCommandExecutor

    @Test
    fun testHeal() {
        val output = commandExecutor.run("heal")
        Assertions.assertThat(output).first().asString().matches("Healed [^ ]+")
    }
}
