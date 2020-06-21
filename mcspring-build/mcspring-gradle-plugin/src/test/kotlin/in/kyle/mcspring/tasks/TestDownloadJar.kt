package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.runGradle
import `in`.kyle.mcspring.writeBaseGradleConfig
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.file.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome

class TestDownloadJar : FreeSpec({

    "should download the Spigot jar" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle.kts"
        writeBaseGradleConfig(buildFile)

        val result = runGradle(folder, "downloadJar")
        val task = result.task(":downloadJar")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val spigot = folder / "spigot" / "spigot.jar"
        spigot.shouldNotBeEmpty()
    }
})
