package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.GradleContext
import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.plusAssign
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.gradle.testkit.runner.TaskOutcome

class TestBuildPluginYml : FreeSpec({

    "should write minimum required info" - {
        val gradle = GradleContext.setup()
        gradle.buildFile += """version = "29" """

        val task = gradle.runTask("buildPluginYml")
        task.outcome shouldBe TaskOutcome.SUCCESS

        gradle.pluginYmlContents.assertSoftly {
            it.size shouldBe 4
            it["name"] shouldBe "test"
            it["version"] shouldBe "29"
            it["main"].toString() shouldEndWith ".SpringJavaPlugin"
            it["spring-boot-main"] shouldBe "ExampleMain"
        }
    }

    "should write all user-specified info properly" - {
        val gradle = GradleContext.setup()
        gradle.buildFile += """
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

        val task = gradle.runTask("buildPluginYml")
        task.outcome shouldBe TaskOutcome.SUCCESS

        gradle.pluginYmlContents.assertSoftly {
            it["description"] shouldBe "test description"
            it["load"] shouldBe "startup"
            it["author"] shouldBe "kyle"
            it["authors"] shouldBe listOf("kyle1", "kyle2")
            it["website"] shouldBe "github"
            it["database"] shouldBe true
            it["prefix"] shouldBe "prefix"
            it["loadbefore"] shouldBe listOf("other")
        }
    }

    "should write dependency tags" - {
        val gradle = GradleContext.setup()

        (gradle.kotlinSourceFolder / "Example.kt") += """
            |import `in`.kyle.mcspring.annotation.*
            |@PluginDepend("plugin")
            |@SoftPluginDepend("plugin2")
            |class Example
        """.trimMargin()

        val task = gradle.runTask("buildPluginYml")
        task.outcome shouldBe TaskOutcome.SUCCESS

        gradle.pluginYmlContents.assertSoftly {
            it["depend"] shouldBe listOf("plugin")
            it["softdepend"] shouldBe listOf("plugin2")
        }
    }

    "should write commands" - {
        val gradle = GradleContext.setup()

        (gradle.kotlinSourceFolder / "Command.kt") += """
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

        val task = gradle.runTask("buildPluginYml")
        task.outcome shouldBe TaskOutcome.SUCCESS

        gradle.pluginYmlContents["commands"] shouldBe mapOf(
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
        val gradle = GradleContext.setup()

        (gradle.kotlinSourceFolder / "Command.kt") += """
            |import `in`.kyle.mcspring.commands.dsl.mcspring.Command
            |@Command("test")
            |fun test() { }
        """.trimMargin()

        val task = gradle.runTask("buildPluginYml")
        task.outcome shouldBe TaskOutcome.SUCCESS

        gradle.pluginYmlContents["commands"] shouldBe mapOf("test" to emptyMap<Any, Any>())
    }
})
