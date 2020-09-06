package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.getMainSourceSet
import `in`.kyle.mcspring.mcspring
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.ZipEntryCompression
import org.gradle.kotlin.dsl.create
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.concurrent.Callable

open class BuildPluginJar : ShadowJar() {

    private val springJarTask by lazy {
        project.tasks.create<BootJar>("springJar") {
            doFirst {
                val libs = project.buildDir / "libs"
                libs.deleteRecursively()
                libs.mkdirs()
            }

            mainClassName = "ignored"
            classpath(Callable { project.getMainSourceSet().runtimeClasspath })

            isExcludeDevtools = false

            archiveFileName.set("spring.jar")
            dependsOn(project.tasks.named("classes"))
        }
    }

    private val javaPluginMain = Callable {
        project.getMainSourceSet().runtimeClasspath.filter {
            "mcspring-base" in it.name
        }.map { file ->
            project.zipTree(file).matching {
                include {
                    it.isDirectory || "in/kyle/mcspring/javaplugin" in it.path
                }
            }
        }
    }

    init {
        SpringBootExtension(project).buildInfo()
        setup()
    }

    private fun setup() {
        entryCompression = ZipEntryCompression.STORED
        includeEmptyDirs = false
        val name = project.extensions.mcspring.pluginName ?: project.name
        archiveBaseName.set(name)
        archiveClassifier.set("")

        val springJar = project.buildDir / "libs" / "spring.jar"
        from(springJar)
        from(javaPluginMain)

        relocate("BOOT-INF/classes/", "")

        doLast {
            springJar.delete()
        }

        dependsOn("buildPluginYml", springJarTask)
    }

    @TaskAction
    override fun copy() {
        relocate(
                "org.springframework.boot.loader.in.kyle.mcspring.javaplugin",
                project.extensions.mcspring.pluginMainPackage
        )
        super.copy()
    }
}
