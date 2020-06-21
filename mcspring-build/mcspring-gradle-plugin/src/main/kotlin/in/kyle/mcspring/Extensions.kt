package `in`.kyle.mcspring

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.findByType
import java.io.File

fun Project.getMainSourceSet(): SourceSet {
    val convention = try {
        project.convention.getPlugin(JavaPluginConvention::class.java)
    } catch (e: IllegalStateException) {
        error("Kotlin/Java/Other plugin not found. Make sure to add one of these.")
    }
    return convention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
}

operator fun File.div(string: String) = resolve(string)

val ExtensionContainer.mcspring: McSpringExtension
    get() = findByType()!!

