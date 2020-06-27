package `in`.kyle.mcspring

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

fun runGradle(folder: File, vararg args: String): BuildResult {
    return GradleRunner.create()
            .withProjectDir(folder)
            .withArguments(
                    *args,
                    "--stacktrace",
                    "--info",
                    "--scan",
                    "-s"
            )
            .withPluginClasspath(getPluginClasspath())
            .withDebug(true)
            .build()
}

private fun getPluginClasspath(): List<File> {
    val pluginClasspathResource = {}::class.java.classLoader.getResource("plugin-classpath.txt")
            ?: error("Did not find plugin classpath resource, run `testClasses` build task.")
    return pluginClasspathResource.readText().lines().map { File(it) }
}

operator fun File.plusAssign(string: String) {
    parentFile.mkdirs()
    appendText("\n$string")
}

operator fun File.div(string: String) = resolve(string)

