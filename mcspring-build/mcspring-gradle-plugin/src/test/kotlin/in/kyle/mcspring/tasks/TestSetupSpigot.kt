package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.plusAssign
import `in`.kyle.mcspring.runGradle
import `in`.kyle.mcspring.writeBaseGradleConfig
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.file.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome

class TestSetupSpigot : FreeSpec({

    "setup spigot should make required files" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle.kts"
        writeBaseGradleConfig(buildFile)
        val spigotJar = folder / "spigot" / "spigot.jar"
        spigotJar.parentFile.mkdirs()
        spigotJar.createNewFile()

        val result = runGradle(folder, "setupSpigot")
        val task = result.task(":setupSpigot")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val spigot = folder/"spigot"
        assertSoftly {
            (spigot / "bukkit.yml").shouldNotBeEmpty()
            (spigot / "eula.txt").shouldNotBeEmpty()
            (spigot / "README.md").shouldNotBeEmpty()
            (spigot / "server.properties").shouldNotBeEmpty()
            (spigot / "spigot.yml").shouldNotBeEmpty()
        }
    }
})
