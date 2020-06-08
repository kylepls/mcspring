package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import org.springframework.stereotype.Component

@Component
internal class CommandClassLoader {

    @Command(
            value = "classloader",
            aliases = ["cl"],
            description = "Show ClassLoader information for a specific class",
            usage = "/classloader <class>"
    )
    fun classLoader() = commandExecutor {
        val className by stringArg {
            missing {
                message("Usage: /$label <class>")
            }
        }

        then { message(executeClassLoader(className)) }
    }

    private fun executeClassLoader(clazz: String): String {
        val aClass = Class.forName(clazz)
        val classLoader = aClass.classLoader.toString()
        val protectionDomain = aClass.protectionDomain.codeSource.location.toString()
        return """
               ClassLoader: $classLoader
               Domain: $protectionDomain
               """.trimIndent()
    }
}
