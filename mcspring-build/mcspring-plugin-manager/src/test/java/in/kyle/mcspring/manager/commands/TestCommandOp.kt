package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
internal class TestCommandOp {

    @Autowired
    lateinit var executor: TestCommandExecutor

    @Test
    fun testOpSelf() {
        val messages = executor.run("op")
        assertThat(messages).hasSize(1)
        assertThat(messages[0]).matches("[^ ]+ is now op")
    }

    @Test
    fun testOpOther() {
        // TODO: 2020-03-13 Need better testing instrumentation
        //        server.getOnlinePlayers().add(target);
        //        List<String> messages = executor.run("op " + target.getName());
        //        assertThat(messages).hasSize(1);
        //        assertThat(messages.get(0)).matches(target.getName() + " is now op");
    }
}
