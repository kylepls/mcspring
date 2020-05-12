package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
internal class TestCommandSpeed() {

    @Autowired
    lateinit var executor: TestCommandExecutor

    @Test
    fun testSpeed() {
        val (sender, messages) = executor.makeTestPlayer()
        executor.run(sender, "speed 10")
        assertThat(messages).first().asString().matches("Speed set to [^ ]+")
        verify(sender, times(1)).walkSpeed = 10F
        verify(sender, times(1)).flySpeed = 10F
    }

    @Test
    fun testSpeedUsage() {
        val (sender, messages) = executor.makeTestPlayer()
        executor.run(sender, "speed")
        assertThat(messages).first().asString().matches("Usage: .+")
        verify(sender, never()).walkSpeed = anyFloat()
        verify(sender, never()).flySpeed = anyFloat()
    }
}
