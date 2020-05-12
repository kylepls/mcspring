package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.test.MCSpringTest
import `in`.kyle.mcspring.test.command.TestCommandExecutor
import org.assertj.core.api.Assertions.assertThat
import org.bukkit.GameMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired

@MCSpringTest
class TestCommandGamemode {

    @Autowired
    lateinit var executor: TestCommandExecutor

    @ParameterizedTest
    @CsvSource(
            "gmc,         CREATIVE",
            "gms,         SURVIVAL",
            "gm creative, CREATIVE",
            "gm survival, SURVIVAL",
            "gm 1,        CREATIVE",
            "gm 0,        SURVIVAL"
    )
    fun testGamemodes(command: String, targetGameMode: GameMode) {
        val (sender, messages) = executor.makeTestPlayer()
        executor.run(sender, command)
        assertThat(messages).first().asString()
                .isEqualTo("Game mode set to ${targetGameMode.name.toLowerCase()}")
        verify(sender, times(1)).gameMode = targetGameMode
    }
}
