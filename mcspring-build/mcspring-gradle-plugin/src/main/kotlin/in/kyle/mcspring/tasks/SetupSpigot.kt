package `in`.kyle.mcspring.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SetupSpigot : DefaultTask() {

    @TaskAction
    fun setup() {
        val directory = project.projectDir.resolve("spigot")
        require(directory.exists()) {"$directory does not yet exist, run downloadJar first"}

        copyResource("/bukkit.yml", directory)
        copyResource("/eula.txt", directory)
        copyResource("/server.properties", directory)
        copyResource("/spigot.yml", directory)
        copyResource("/README.md", directory)
    }

    private fun copyResource(path: String, directory: File) {
        val target = directory.resolve(path.substring(1))
        if (target.exists()) {
            logger.info("Target file $target already exists, skipping...")
            return
        }

        target.parentFile.mkdirs()
        target.outputStream().use { os ->
            javaClass.getResourceAsStream(path).use {
                requireNotNull(it) {"Could not find $path"}
                it.copyTo(os)
            }
        }
    }
}
