package `in`.kyle.mcspring.tasks

import `in`.kyle.mcspring.div
import `in`.kyle.mcspring.plusAssign
import `in`.kyle.mcspring.runGradle
import `in`.kyle.mcspring.writeBaseGradleConfig
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
        val folder = createTempDir()
        val buildFile = folder / "build.gradle"
        writeBaseGradleConfig(buildFile)
        buildFile += """
            |repositories {
            |   mavenCentral()
            |   mavenLocal()
            |}
            |dependencies {
            |   implementation "org.jetbrains.kotlin:kotlin-stdlib"
            |   implementation "in.kyle.mcspring:mcspring-base:+" 
            |}
            |mcspring {
            |   pluginMainPackage = "main"
            |}
        """.trimMargin()

        val srcFile = folder / "src" / "main" / "kotlin" / "Base.kt"
        srcFile += "fun test() { }"

        val result = runGradle(folder, "buildPluginJar")
        val task = result.task(":buildPluginJar")!!
        task.outcome shouldBe TaskOutcome.SUCCESS

        val pluginJar = (folder / "build" / "libs").listFiles()?.firstOrNull()
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
                    scan.allClasses.filter { it.packageName == "main" }
                            .map { it.name } shouldBe listOf("main.SpringJavaPlugin")
                }
            }

            "should have source files" - {
                scan.allClasses.filter { it.name == "BaseKt" } shouldHaveSize 1
            }
        }
    }
})
