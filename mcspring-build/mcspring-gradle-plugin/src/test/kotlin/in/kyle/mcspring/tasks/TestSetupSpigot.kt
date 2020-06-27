package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.GradleContext
import `in`.kyle.mcspring.div
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.file.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome

class TestSetupSpigot : FreeSpec({

    "setup spigot should make required files" - {
        val gradle = GradleContext.setup()
        val spigotJar = gradle.spigotFolder / "spigot.jar"
        spigotJar.parentFile.mkdirs()
        spigotJar.createNewFile()

        val task = gradle.runTask("setupSpigot")
        task.outcome shouldBe TaskOutcome.SUCCESS

        val spigot = gradle.spigotFolder
        assertSoftly {
            (spigot / "bukkit.yml").shouldNotBeEmpty()
            (spigot / "eula.txt").shouldNotBeEmpty()
            (spigot / "README.md").shouldNotBeEmpty()
            (spigot / "server.properties").shouldNotBeEmpty()
            (spigot / "spigot.yml").shouldNotBeEmpty()
        }
    }
})
