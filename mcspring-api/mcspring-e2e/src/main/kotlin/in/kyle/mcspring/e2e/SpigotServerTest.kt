package `in`.kyle.mcspring.e2e

import io.mockk.every
import io.mockk.mockk
import io.papermc.paperclip.Paperclip
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginDescriptionFile
import org.junit.jupiter.api.extension.*
import org.junit.platform.commons.JUnitException
import java.io.File
import java.lang.reflect.Method

class SpigotServerTest : BeforeAllCallback, AfterAllCallback, InvocationInterceptor {

    override fun beforeAll(context: ExtensionContext) {
        startSpigot()
    }

    override fun afterAll(context: ExtensionContext) {
        println("Shutting down")
        Bukkit.getServer().shutdown()
        cleanupFiles()
        // let junit clean up the zombie threads
    }

    override fun interceptTestMethod(
            invocation: InvocationInterceptor.Invocation<Void>,
            invocationContext: ReflectiveInvocationContext<Method>,
            extensionContext: ExtensionContext?
    ) {
        val plugin = mockk<Plugin>()
        every { plugin.isEnabled } returns true
        every { plugin.name } returns "test-instance"
        every { plugin.description } returns PluginDescriptionFile("test-instance", "0.0.1", "main-class")
        every { plugin.logger } returns Bukkit.getLogger()
        every { plugin.server } returns Bukkit.getServer()

        var hasRun = false
        Bukkit.getServer().scheduler.runTask(plugin, Runnable {
            invocation.proceed()
            hasRun = true
        })

        while (!hasRun) {
            Thread.sleep(1)
        }
        println("Run test")
    }

    private fun startSpigot() {
        try {
            startSpigotServer()
            waitForSpigotToStart()
        } catch (e: Exception) {
            throw JUnitException("Could not start Spigot server", e)
        }
    }

    private fun findSpigotMain(): Method {
        return try {
            Class.forName("org.bukkit.craftbukkit.Main")
                    .declaredMethods
                    .first { it.name == "main" }
        } catch (e: ClassNotFoundException) {
            error("""
                    Could not find Spigot jar main class on the runtime classpath.
                """.trimIndent())
        }
    }

    private fun startSpigotServer() {
        System.setProperty("IReallyKnowWhatIAmDoingISwear", "true")
        System.setProperty("com.mojang.eula.agree", "true")

        val args = arrayOf("-nogui", "-o=false")
        Thread(Runnable {
            try {
                Paperclip.main(args)
            } catch (e: Exception) {
                println(e)
            }
        }, "spigot-thread").start()
    }

    private fun waitForSpigotToStart() {
        @Suppress("UNNECESSARY_SAFE_CALL", "SENSELESS_COMPARISON")
        while (Bukkit.getServer() == null || Bukkit.getWorlds().isEmpty()) {
            Thread.sleep(1)
        }

        println("Spigot Server ${Bukkit.getVersion()} ready")
    }

    private fun cleanupFiles() {
        File("")() {
            -"logs"
            -"plugins"
            -"cache"
            -"world"
            -"banned-ips.json"
            -"banned-players.json"
            -"bukkit.yml"
            -"commands.yml"
            -"eula.txt"
            -"help.yml"
            -"ops.json"
            -"paper.yml"
            -"server.properties"
            -"spigot.yml"
            -"version_history.json"
            -"whitelist.json"
            -"permissions.yml"
            -"usercache.json"
            -"world_nether"
            -"world_the_end"
            -"crash-reports"
        }
    }
}
