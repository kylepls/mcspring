package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.GradleContext
import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.runGradle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.file.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome

class TestDownloadJar : FreeSpec({

    "should download the Spigot jar" - {
        val gradle = GradleContext.setup()

        val task = gradle.runTask("downloadJar")
        task.outcome shouldBe TaskOutcome.SUCCESS

        val spigot = gradle.spigotFolder / "spigot.jar"
        spigot.shouldNotBeEmpty()
    }
})
