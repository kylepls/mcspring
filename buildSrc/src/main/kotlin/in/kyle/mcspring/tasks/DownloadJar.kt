package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.McSpringExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.findByType
import java.io.File
import java.net.URI

open class DownloadJar : DefaultTask() {

    @TaskAction
    fun download() {
        val props = project.extensions.findByType<McSpringExtension>()
        requireNotNull(props) { "mcspring not defined in build.gradle" }

        val target = File(props.spigotDirectory).resolve("spigot.jar")
        target.parentFile.mkdirs()
        if (target.exists()) {
            logger.info("Jar already exists, skipping download")
            return
        }

        target.delete()

        target.outputStream().use { os ->
            URI(props.spigotDownloadUrl).toURL().openStream().use {
                it.copyTo(os)
            }
        }
    }
}
