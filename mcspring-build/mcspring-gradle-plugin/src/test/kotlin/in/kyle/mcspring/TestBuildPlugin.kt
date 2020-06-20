package `in`.kyle.mcspring

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import org.gradle.testkit.runner.UnexpectedBuildFailure

class TestBuildPlugin : FreeSpec({

    "should throw exception if missing source plugin" - {
        val folder = createTempDir()
        val buildFile = folder.resolve("build.gradle.kts")

        buildFile += """
            |plugins {
            |   id("in.kyle.mcspring")
            |}
        """.trimMargin()

        shouldThrow<UnexpectedBuildFailure> {
            runGradle(folder, "build")
        }
    }
})
