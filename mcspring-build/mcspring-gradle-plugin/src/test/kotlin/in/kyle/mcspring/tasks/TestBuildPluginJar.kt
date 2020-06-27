package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.*
import io.github.classgraph.ClassGraph
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import java.net.URLClassLoader

class TestBuildPluginJar : FreeSpec({

    "should create proper plugin jar" - {
        val gradle = GradleContext.setup()

        (gradle.kotlinSourceFolder / "Base.kt") += "fun test() { }"

        val task = gradle.runTask("buildPluginJar")
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginJar = gradle.libsFolder.listFiles()?.firstOrNull()
                ?: error("Plugin jar not generated")

        val classLoader = URLClassLoader(arrayOf(pluginJar.toURI().toURL()))
        val scan = ClassGraph()
                .enableClassInfo()
                .overrideClassLoaders(classLoader)
                .scan()
        scan.use {
            "should have plugin.yml" - {
                scan.getResourcesWithPath("plugin.yml") shouldHaveSize 1
            }

            "all jar files should be in lib directory" - {
                scan.getResourcesWithExtension("jar").map { it.path }.forAll {
                    it.startsWith("BOOT-INF/lib/")
                }
            }

            "plugin main should be relocated" - {
                assertSoftly {
                    scan.allClasses.filter { it.packageName == "test.plugin" }
                            .map { it.name } shouldBe listOf("test.plugin.SpringJavaPlugin")
                }
            }

            "should have source files" - {
                scan.allClasses.filter { it.name == "BaseKt" } shouldHaveSize 1
            }
        }
    }
})
