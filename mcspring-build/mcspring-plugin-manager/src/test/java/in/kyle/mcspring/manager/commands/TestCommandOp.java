package in.kyle.mcspring.manager.commands;

import org.bukkit.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import in.kyle.mcspring.test.command.TestSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
class TestCommandOp {
    
    @Autowired
    TestCommandExecutor executor;
    
    @Autowired
    Server server;
    
    TestSender sender;
    
    @BeforeEach
    void setup() {
        sender = spy(TestSender.class);
    }
    
    @Test
    void testOpSelf() {
        doReturn(true).when(sender).isOp();
        doReturn("wendy").when(sender).getName();
        executor.run(sender, "op");
        assertThat(sender.getMessages()).containsExactly("wendy is now op");
    }
    
    @Test
    void testOpOther() {
        TestSender target = mock(TestSender.class);
        doReturn("wendy").when(target).getName();
        doReturn(target).when(server).getPlayerExact("wendy");
        doReturn(true).when(target).isOp();
        List<String> messages = executor.run("op " + target.getName());
        assertThat(messages).containsExactly(target.getName() + " is now op");
    }
}
