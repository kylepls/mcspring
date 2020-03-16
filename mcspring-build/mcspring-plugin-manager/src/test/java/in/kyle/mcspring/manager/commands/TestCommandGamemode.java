package in.kyle.mcspring.manager.commands;

import org.bukkit.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;
import in.kyle.mcspring.test.command.TestSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MCSpringTest
public class TestCommandGamemode {
    
    @Autowired
    TestCommandExecutor executor;
    
    TestSender sender;
    
    @BeforeEach
    void setup() {
        sender = spy(TestSender.class);
    }
    
    @Test
    void testGmc() {
        executor.run(sender, "gmc");
        assertThat(sender.getMessages()).containsExactly("Game mode set to creative");
        verify(sender).setGameMode(GameMode.CREATIVE);
    }
    
    @Test
    void testGms() {
        executor.run(sender, "gms");
        assertThat(sender.getMessages()).containsExactly("Game mode set to survival");
        verify(sender).setGameMode(GameMode.SURVIVAL);
    }
    
    @Test
    void testGamemode() {
        executor.run(sender, "gm creative");
        assertThat(sender.getMessages()).containsExactly("Game mode set to creative");
        verify(sender).setGameMode(GameMode.CREATIVE);
    }
    
    @Test
    void testGamemodeNumeric() {
        executor.run(sender, "gm 1");
        assertThat(sender.getMessages()).containsExactly("Game mode set to creative");
        verify(sender).setGameMode(GameMode.CREATIVE);
    }
}
