package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
internal class TestCommandSpeed {
//
//    @Autowired
//    lateinit var executor: TestCommandExecutor
//    @Autowired
//    lateinit var sender: TestPlayer
//
//    @Test
//    fun testSpeed() {
//        val messages = executor.run(sender, "speed 10")
//        assertThat(messages).first().asString().matches("Speed set to [^ ]+")
//        assertThat(sender.walkSpeed).isEqualTo(10f)
//        assertThat(sender.flySpeed).isEqualTo(10f)
//    }
//
//    @Test
//    fun testSpeedUsage() {
//        sender.walkSpeed = 0f
//        sender.flySpeed = 0f
//        val messages = executor.run(sender, "speed")
//        assertThat(messages).first().asString().startsWith("Usage: ")
//        assertThat(sender.walkSpeed).isEqualTo(0f)
//        assertThat(sender.flySpeed).isEqualTo(0f)
//    }
}
