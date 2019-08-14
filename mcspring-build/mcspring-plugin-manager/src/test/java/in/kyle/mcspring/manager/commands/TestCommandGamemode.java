package in.kyle.mcspring.manager.commands;

import org.bukkit.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import in.kyle.api.bukkit.entity.TestPlayer;
import in.kyle.mcspring.test.MCSpringTest;
import in.kyle.mcspring.test.command.TestCommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@MCSpringTest
public class TestCommandGamemode {
    
    @Autowired
    TestCommandExecutor executor;
    
    @Autowired
    TestPlayer sender;
    
    @BeforeEach
    void setup() {
        sender.setGameMode(GameMode.SURVIVAL);
    }
    
    @Test
    void testGmc() {
        List<String> output = executor.run(sender, "gmc");
        assertThat(output).first().asString().isEqualTo("Game mode set to creative");
        assertThat(sender.getGameMode()).isEqualTo(GameMode.CREATIVE);
    }
    
    @Test
    void testGms() {
        sender.setGameMode(GameMode.SPECTATOR);
        List<String> output = executor.run(sender, "gms");
        assertThat(output).first().asString().isEqualTo("Game mode set to survival");
        assertThat(sender.getGameMode()).isEqualTo(GameMode.SURVIVAL);
    }
    
    @Test
    void testGamemode() {
        List<String> output = executor.run(sender, "gm creative");
        assertThat(output).first().asString().isEqualTo("Game mode set to creative");
        assertThat(sender.getGameMode()).isEqualTo(GameMode.CREATIVE);
    }
    
    @Test
    void testGamemodeNumeric() {
        List<String> output = executor.run(sender, "gm 1");
        assertThat(output).first().asString().isEqualTo("Game mode set to creative");
        assertThat(sender.getGameMode()).isEqualTo(GameMode.CREATIVE);
    }
}
