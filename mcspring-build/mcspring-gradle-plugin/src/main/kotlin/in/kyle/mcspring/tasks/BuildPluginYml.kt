package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.McSpringExtension
import io.github.classgraph.ClassGraph
import org.apache.log4j.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.URLClassLoader

open class BuildPluginYml : DefaultTask() {

    private val logger = Logger.getLogger(BuildPluginYml::class.java)
    private val attributes = mutableMapOf<String, Any>()

    @TaskAction
    fun buildYml() {
        val props = project.extensions.findByType<McSpringExtension>()
        requireNotNull(props) { "mcspring not defined in build.gradle" }

        with(props) {
            attributes["name"] = pluginName ?: project.name
            attributes["version"] = pluginVersion ?: project.version
            attributes["main"] = "$pluginMainPackage.SpringJavaPlugin"

            pluginDescription?.apply { attributes["description"] = this }
            pluginLoad?.apply {
                val value = when (this) {
                    is McSpringExtension.Load -> this.name.toLowerCase()
                    is String -> this
                    else -> error("Invalid pluginLoad value $this")
                }
                attributes["load"] = value
            }
            pluginAuthor?.apply { attributes["author"] = this }
            pluginAuthors?.apply { attributes["authors"] = this }
            pluginWebsite?.apply { attributes["website"] = this }
            pluginDatabase?.apply { attributes["database"] = this }
            pluginPrefix?.apply { attributes["prefix"] = this }
            pluginLoadBefore?.apply { attributes["loadbefore"] = this }

            // TODO commands
            addDependencies(project.configurations["runtime"].files)

            val outputFile = project.buildDir.resolve("resources").resolve("main")
            outputFile.mkdirs()
            val resolve = outputFile.resolve("plugin.yml")
            logger.info("Building to $resolve")
            resolve.writeText(Yaml().dump(attributes))
        }
    }

    private fun addDependencies(files: Set<File>) {
        val classLoader = URLClassLoader(files.map { it.toURI().toURL() }.toTypedArray())
        val scan = ClassGraph().verbose().overrideClassLoaders(classLoader).enableAnnotationInfo().scan()

        fun getPluginDependencies(annotation: String) =
                scan.allClasses.filter { it.isStandardClass && it.hasAnnotation(annotation) }
                        .map { it.getAnnotationInfo(annotation).parameterValues }
                        .flatMap { (it["plugins"].value as Array<String>).toList() }
        getPluginDependencies("in.kyle.mcspring.annotation.SoftPluginDepend").apply {
            if (this.isNotEmpty()) {
                attributes["softdepend"] = this
            }
        }
        getPluginDependencies("in.kyle.mcspring.annotation.PluginDepend").apply {
            if (this.isNotEmpty()) {
                attributes["depend"] = this
            }
        }
    }
}
