package `in`.kyle.mcspring

import `in`.kyle.mcspring.tasks.BuildPluginJar
import `in`.kyle.mcspring.tasks.BuildPluginYml
import `in`.kyle.mcspring.tasks.DownloadJar
import `in`.kyle.mcspring.tasks.SetupSpigot
import org.codehaus.groovy.ast.ClassHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.File

class BuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create<McSpringExtension>("mcspring", project)

        project.tasks.register<BuildPluginYml>("buildPluginYml") {
            description = "Generates a and plugin.yml"
            dependsOn(project.tasks.findByName("classes"))
        }

        project.tasks.register<DownloadJar>("downloadJar") {
            description = "Download the Bukkit jar"
        }

        project.tasks.register<SetupSpigot>("setupSpigot") {
            description = "Adds default Spigot configuration settings"
        }

        project.tasks.register<BuildPluginJar>("buildPluginJar") {
            description = "Builds the plugin jar with dependencies"
        }

        project.tasks.create<Copy>("copyJarToSpigot") {
            description = "Copy the built plugin jar to the Spigot directory"
            val props = project.extensions.mcspring
            from(project.buildDir / "libs")
            val pluginsDir = File(props.spigotDirectory) / "plugins"
            doFirst {
                pluginsDir.mkdirs()
            }
            into(pluginsDir)
        }

        project.tasks.register("buildServer") {
            group = "mcspring"
            description = "Performs a complete sever assembly (this is what you're looking for)"
            dependsOn(
                    "downloadJar",
                    "setupSpigot",
                    "buildPluginYml",
                    "buildPluginJar",
                    "copyJarToSpigot"
            )
        }

        project.tasks.named("jar") { onlyIf { false } }

        registerTestDependencies(project)
        registerMcSpringDependencies(project)
    }

    private fun registerTestDependencies(project: Project) {
        project.afterEvaluate {
            val extension = project.extensions.mcspring
            project.dependencies.add(
                    "testImplementation",
                    project.paperRunner(extension.spigotVersion)
            )
        }
    }

    private fun registerMcSpringDependencies(project: Project) {
        project.configurations.all {
            resolutionStrategy.eachDependency {
                if (requested.group == "in.kyle.mcspring" && requested.version?.isBlank() == true) {
                    val version = BuildPlugin::class.java.`package`.implementationVersion
                    useVersion(version)
                    because("""
                        mcspring dependencies with missing versions are set to the build plugin's version.
                    """.trimIndent())
                }
            }
        }
    }
}
