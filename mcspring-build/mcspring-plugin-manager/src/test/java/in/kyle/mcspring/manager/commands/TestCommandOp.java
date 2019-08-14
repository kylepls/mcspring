package in.kyle.mcspring.manager.commands;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@MCSpringTest
class TestCommandOp {
    
    @Autowired
    TestCommandExecutor executor;
    
    @Test
    void testOpSelf() {
        List<String> messages = executor.run("op");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).matches("[^ ]+ is now op");
    }
    
    @Test
    void testOpOther() {
        // TODO: 2020-03-13 Need better testing instrumentation
        
        //        server.getOnlinePlayers().add(target);
        //        List<String> messages = executor.run("op " + target.getName());
        //        assertThat(messages).hasSize(1);
        //        assertThat(messages.get(0)).matches(target.getName() + " is now op");
    }
}
