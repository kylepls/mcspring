package `in`.kyle.mcspring

import org.gradle.api.Project

open class McSpringExtension(project: Project) {

    var spigotVersion: String = "1.15.2"
    var spigotDownloadUrl: String = "https://papermc.io/api/v1/paper/$spigotVersion/latest/download"
    var spigotDirectory: String = project.projectDir.resolve("spigot").absolutePath

    var pluginMainPackage: String? = null
    var pluginName: String? = null
    var pluginVersion: String? = null
    var pluginDescription: String? = project.description
    var pluginLoad: Any? = null
    var pluginAuthor: String? = null
    var pluginAuthors: List<String>? = null
    var pluginWebsite: String? = null
    var pluginDatabase: Boolean? = null
    var pluginPrefix: String? = null
    var pluginLoadBefore: List<String>? = null
    var pluginApiVersion: String? = null

    fun latestApiVersion() {
        pluginApiVersion = "1.13"
    }

    enum class Load {
        STARTUP, POSTWORLD
    }
}
