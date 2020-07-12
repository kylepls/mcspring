package `in`.kyle.mcspring

import org.yaml.snakeyaml.Yaml
import java.io.File

class GradleContext private constructor() {

    val folder = createTempDir()
    val buildFile by lazy { folder / "build.gradle" }
    val kotlinSourceFolder by lazy { folder / "src" / "main" / "kotlin" }
    val pluginYml by lazy { folder / "build" / "resources" / "main" / "plugin.yml" }
    val libsFolder by lazy { folder / "build" / "libs" }
    val spigotFolder by lazy { folder / "spigot" }

    val pluginYmlContents: Map<String, Any>
        get() = Yaml().load(pluginYml.inputStream())

    fun runTask(goal: String) = runGradle(folder, goal).task(":$goal")!!

    private fun writeMainClass() {
        val srcFile = kotlinSourceFolder / "ExampleMain.kt"
        srcFile += """
            |import org.springframework.boot.autoconfigure.SpringBootApplication
            |@SpringBootApplication
            |open class ExampleMain
        """.trimMargin()
    }

    companion object {
        fun setup(): GradleContext {
            val context = GradleContext()
            context.writeMainClass()
            writeBaseGradleConfig(context.buildFile)
            return context
        }
    }
}

private fun writeBaseGradleConfig(file: File) {
    file += """
        |plugins {
        |   id("org.jetbrains.kotlin.jvm") version "1.3.72"
        |   id("in.kyle.mcspring")
        |}
        |
        |repositories {
        |   mavenLocal()
        |   jcenter()
        |}
        |dependencies {
        |   implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        |   implementation("in.kyle.mcspring:mcspring-base:+")
        |   implementation("in.kyle.mcspring:mcspring-commands-dsl:+")
        |}
        |mcspring {
        |   pluginName = "test"
        |   pluginMainPackage = "test.plugin"
        |}
    """.trimMargin()
    val settings = file.parentFile / "settings.gradle.kts"
    settings += """
        |plugins {
        |   id("com.gradle.enterprise").version("3.3.4")
        |}
        |gradleEnterprise {
        |    buildScan {
        |        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        |        termsOfServiceAgree = "yes"
        |    }
        |}
    """.trimMargin()
    val gradleProperties = file.parentFile / "gradle.properties"
    gradleProperties += """
        org.gradle.jvmargs=-Xmx1024m
    """.trimIndent()
}
