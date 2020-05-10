package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
class TestCommandGamemode {
//
//    @Autowired
//    lateinit var executor: TestCommandExecutor
//    @Autowired
//    lateinit var sender: Player
//
//    @Test
//    fun testGmc() {
//        val output = executor.run(sender, "gmc")
//        assertThat(output).first().asString().isEqualTo("Game mode set to creative")
//        assertThat(sender.gameMode).isEqualTo(GameMode.CREATIVE)
//    }
//
//    @Test
//    fun testGms() {
//        sender.gameMode = GameMode.SPECTATOR
//        val output = executor.run(sender, "gms")
//        assertThat(output).first().asString().isEqualTo("Game mode set to survival")
//        assertThat(sender.gameMode).isEqualTo(GameMode.SURVIVAL)
//    }
//
//    @Test
//    fun testGamemode() {
//        val output = executor.run(sender, "gm creative")
//        assertThat(output).first().asString().isEqualTo("Game mode set to creative")
//        assertThat(sender.gameMode).isEqualTo(GameMode.CREATIVE)
//    }
//
//    @Test
//    fun testGamemodeNumeric() {
//        val output = executor.run(sender, "gm 1")
//        assertThat(output).first().asString().isEqualTo("Game mode set to creative")
//        assertThat(sender.gameMode).isEqualTo(GameMode.CREATIVE)
//    }
}
