package `in`.kyle.mcspring

import `in`.kyle.mcspring.tasks.BuildPluginYml
import org.gradle.api.Project

open class McSpringExtension(project: Project) {

    var spigotVersion: String = "1.15.2"
    var spigotDownloadUrl: String = "https://papermc.io/api/v1/paper/$spigotVersion/latest/download"
    var spigotDirectory: String = project.projectDir.resolve("spigot").absolutePath

    var pluginMainPackage: String = "${project.group}.${project.name}"
    var pluginName: String = project.name
    var pluginVersion: String = project.version.toString().takeIf { it != "unspecified" } ?: "0.0.1"
    var pluginDescription: String? = project.description
    var pluginLoad: BuildPluginYml.Load? = null
    var pluginAuthor: String? = null
    var pluginAuthors: List<String>? = null
    var pluginWebsite: String? = null
    var pluginDatabase: Boolean? = null
    var pluginPrefix: String? = null
    var pluginLoadBefore: List<String>? = null

    val pluginMainClassName: String
        get() = "$pluginMainPackage.SpringPlugin"
}
