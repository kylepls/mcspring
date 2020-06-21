package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.plusAssign
import `in`.kyle.mcspring.runGradle
import `in`.kyle.mcspring.writeBaseGradleConfig
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.gradle.testkit.runner.TaskOutcome
import org.yaml.snakeyaml.Yaml

class TestBuildPluginYml : FreeSpec({

    "should write minimum required info" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle.kts"
        writeBaseGradleConfig(buildFile)
        buildFile += """
            version = "29"
        """.trimIndent()

        val result = runGradle(folder, "buildPluginYml")
        val task = result.task(":buildPluginYml")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginYml = folder / "build" / "resources" / "main" / "plugin.yml"
        val yml = Yaml().load<Map<String, Any>>(pluginYml.inputStream())

        assertSoftly {
            yml.size shouldBe 3
            yml["name"].toString() shouldEndWith ".tmp"
            yml["version"] shouldBe "29"
            yml["main"].toString() shouldEndWith ".SpringJavaPlugin"
        }
    }

    "should write all user-specified info properly" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle"
        writeBaseGradleConfig(buildFile)
        // For some reason this doesn't work with the Kotlin dsl???
        buildFile += """
            |mcspring {
            |   pluginDescription = "test description"
            |   pluginLoad = "startup"
            |   pluginAuthor = "kyle"
            |   pluginWebsite = "github"
            |   pluginDatabase = true
            |   pluginPrefix = "prefix"
            |   pluginAuthors = ["kyle1", "kyle2"]
            |   pluginLoadBefore = ["other"]
            |}
        """.trimMargin()

        val result = runGradle(folder, "buildPluginYml")
        val task = result.task(":buildPluginYml")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginYml = folder / "build" / "resources" / "main" / "plugin.yml"
        val yml = Yaml().load<Map<String, Any>>(pluginYml.inputStream())

        assertSoftly {
            yml["description"] shouldBe "test description"
            yml["load"] shouldBe "startup"
            yml["author"] shouldBe "kyle"
            yml["authors"] shouldBe listOf("kyle1", "kyle2")
            yml["website"] shouldBe "github"
            yml["database"] shouldBe true
            yml["prefix"] shouldBe "prefix"
            yml["loadbefore"] shouldBe listOf("other")
        }
    }

    "should write dependency tags" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle.kts"
        writeBaseGradleConfig(buildFile)
        buildFile += """
            |repositories {
            |   mavenCentral()
            |   mavenLocal()
            |}
            |dependencies {
            |    implementation("in.kyle.mcspring:mcspring-base:+")
            |}
        """.trimMargin()

        val srcFile = folder / "src" / "main" / "kotlin" / "Example.kt"
        srcFile += """
            |import `in`.kyle.mcspring.annotation.*
            |@PluginDepend("plugin")
            |@SoftPluginDepend("plugin2")
            |class Example
        """.trimMargin()

        val result = runGradle(folder, "buildPluginYml")
        val task = result.task(":buildPluginYml")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginYml = folder / "build" / "resources" / "main" / "plugin.yml"
        val yml = Yaml().load<Map<String, Any>>(pluginYml.inputStream())

        assertSoftly {
            yml["depend"] shouldBe listOf("plugin")
            yml["softdepend"] shouldBe listOf("plugin2")
        }
    }

    "should write commands" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle.kts"
        writeBaseGradleConfig(buildFile)
        buildFile += """
            |repositories {
            |   mavenCentral()
            |   mavenLocal()
            |}
            |dependencies {
            |    implementation(kotlin("stdlib"))
            |    implementation("in.kyle.mcspring:mcspring-base:+")
            |    implementation("in.kyle.mcspring:mcspring-commands-dsl:+")
            |}
        """.trimMargin()

        val srcFile = folder / "src" / "main" / "kotlin" / "Command.kt"
        srcFile += """
            |import `in`.kyle.mcspring.commands.dsl.mcspring.Command
            |@Command(
            |        value = "test",
            |        aliases = ["t"],
            |        description = "a test command",
            |        usage = "/test",
            |        permission = "test",
            |        permissionMessage = "no permission"
            |)
            |fun test() { }
        """.trimMargin()

        val result = runGradle(folder, "buildPluginYml")
        val task = result.task(":buildPluginYml")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginYml = folder / "build" / "resources" / "main" / "plugin.yml"
        val yml = Yaml().load<Map<String, Any>>(pluginYml.inputStream())

        yml["commands"] shouldBe mapOf(
                "test" to mapOf(
                        "description" to "a test command",
                        "aliases" to listOf("t"),
                        "permission" to "test",
                        "permission-message" to "no permission",
                        "usage" to "/test"
                )
        )
    }

    "should add minimal info to commands" - {
        val folder = createTempDir()
        val buildFile = folder / "build.gradle.kts"
        writeBaseGradleConfig(buildFile)
        buildFile += """
            |repositories {
            |   mavenCentral()
            |   mavenLocal()
            |}
            |dependencies {
            |    implementation(kotlin("stdlib"))
            |    implementation("in.kyle.mcspring:mcspring-base:+")
            |    implementation("in.kyle.mcspring:mcspring-commands-dsl:+")
            |}
        """.trimMargin()

        val srcFile = folder / "src" / "main" / "kotlin" / "Command.kt"
        srcFile += """
            |import `in`.kyle.mcspring.commands.dsl.mcspring.Command
            |@Command("test")
            |fun test() { }
        """.trimMargin()

        val result = runGradle(folder, "buildPluginYml")
        val task = result.task(":buildPluginYml")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginYml = folder / "build" / "resources" / "main" / "plugin.yml"
        val yml = Yaml().load<Map<String, Any>>(pluginYml.inputStream())

        yml["commands"] shouldBe mapOf("test" to emptyMap<Any, Any>())
    }
})
