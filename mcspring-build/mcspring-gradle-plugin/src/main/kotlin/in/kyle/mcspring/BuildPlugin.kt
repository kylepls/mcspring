package `in`.kyle.mcspring

import `in`.kyle.mcspring.tasks.BuildPluginYml
import `in`.kyle.mcspring.tasks.DownloadJar
import `in`.kyle.mcspring.tasks.SetupSpigot
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.ZipEntryCompression
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.io.File
import java.util.concurrent.Callable

class BuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create<McSpringExtension>("mcspring", project)
        registerBuildPluginYml(project)
        registerBuildPluginJar(project)
        registerDownloadJar(project)
        registerSetupSpigot(project)
        registerCopyToJarToSpigot(project)
        registerBuildServer(project)
    }

    private fun registerBuildServer(project: Project) {
        project.tasks.register("buildServer") {
            group = "mcspring"
            description = "Performs a complete sever assembly (this is what you're looking for)"

            dependsOn(
                    "downloadJar",
                    "setupSpigot",
                    "buildPluginYml",
                    "pluginJar",
                    "copyJarToSpigot"
            )
        }
    }

    private fun registerCopyToJarToSpigot(project: Project) {
        val props = project.extensions.findByType<McSpringExtension>()!!
        project.tasks.create<Copy>("copyJarToSpigot") {
            description = "Copy the built plugin jar to the Spigot directory"

            from(project.buildDir.resolve("libs"))
            val pluginsDir = File(props.spigotDirectory).resolve("plugins")
            pluginsDir.mkdirs()
            mustRunAfter("pluginJar")
            into(pluginsDir)
        }
    }

    private fun registerSetupSpigot(project: Project) {
        project.tasks.register<SetupSpigot>("setupSpigot") {
            description = "Adds default Spigot configuration settings"
        }
    }

    private fun registerDownloadJar(project: Project) {
        project.tasks.register<DownloadJar>("downloadJar") {
            description = "Download the Bukkit jar"
        }
    }

    private fun registerBuildPluginJar(project: Project) {
        val convention = try {
            project.convention.getPlugin(JavaPluginConvention::class.java)
        } catch (e: IllegalStateException) {
            error("Kotlin/Java/Other plugin not found. Make sure to add one of these.")
        }
        val mainSourceSet: SourceSet = convention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        SpringBootExtension(project).buildInfo()

        val springJar = project.tasks.create<BootJar>("springJar") {
            mainClassName = "ignored"

            doFirst {
                project.buildDir.resolve("libs").listFiles()?.forEach { it.delete() }
            }
            classpath(Callable { mainSourceSet.runtimeClasspath })

            archiveFileName.set("spring.jar")
            dependsOn(project.tasks.named("classes"))
        }

        val builtSpringJar = project.buildDir.resolve("libs").resolve("spring.jar")
        project.tasks.create<ShadowJar>("pluginJar") {
            description = "Generates a plugin jar"
//            group = "mcspring"

            entryCompression = ZipEntryCompression.STORED
            from(builtSpringJar)
            archiveClassifier.set("")

            from(Callable {
                mainSourceSet.runtimeClasspath.filter {
                    "mcspring-base" in it.name
                }.map {
                    project.zipTree(it).matching {
                        include {
                            it.isDirectory || "javaplugin" in it.path
                        }
                    }
                }
            })

            relocate("BOOT-INF/classes/", "")
            doFirst {
                val props = project.extensions.findByType<McSpringExtension>()!!
                relocate("in.kyle.mcspring.javaplugin", props.pluginMainPackage)
            }

            includeEmptyDirs = false
            dependsOn(springJar)

            mustRunAfter("buildPluginYml")
            doLast {
                builtSpringJar.delete()
            }
        }

        project.tasks.named("jar") { onlyIf { false } }
    }

    private fun registerBuildPluginYml(project: Project) {
        project.tasks.register<BuildPluginYml>("buildPluginYml") {
            description = "Generates a and plugin.yml"
        }
    }
}
