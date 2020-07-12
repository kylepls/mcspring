package `in`.kyle.mcspring

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.findByType
import java.io.File
import java.net.URL

fun Project.getMainSourceSet(): SourceSet {
    val convention = try {
        project.convention.getPlugin(JavaPluginConvention::class.java)
    } catch (e: IllegalStateException) {
        error("Kotlin/Java/Other plugin not found. Make sure to add one of these.")
    }
    return convention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
}

fun DependencyHandlerScope.mcspring(name: String) = "in.kyle.mcspring:mcspring-$name"

fun Project.paperRunner(version: String): ConfigurableFileCollection {
    return urlFile(this,
            "https://papermc.io/api/v1/paper/$version/latest/download",
            "paper-runner-$version")
}

private fun urlFile(project: Project, url: String, name: String): ConfigurableFileCollection {
    val file = File("${project.buildDir}/downloads/${name}.jar")
    file.parentFile.mkdirs()
    if (!file.exists()) {
        URL(url).openStream().use { downloadStream ->
            file.outputStream().use { fileOut ->
                downloadStream.copyTo(fileOut)
            }
        }
    }
    return project.files(file.absolutePath)
}

operator fun File.div(string: String) = resolve(string)

val ExtensionContainer.mcspring: McSpringExtension
    get() = findByType()!!

