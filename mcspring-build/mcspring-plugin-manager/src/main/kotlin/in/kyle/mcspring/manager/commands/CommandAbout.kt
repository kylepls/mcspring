package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.commands.dsl.commandExecutor
import org.bukkit.Server
import org.springframework.boot.info.BuildProperties
import org.springframework.core.SpringVersion
import org.springframework.stereotype.Component

@Component
internal class CommandAbout(
        private val properties: BuildProperties,
        private val server: Server
) {

    @Command(value = "about",
            description = "Provides information about current library versions in use")
    fun about() = commandExecutor {
        then {
            val aboutString = """
            Plugin Name: ${properties.name}
            Plugin Version: ${properties.version}
            Spring Version: ${SpringVersion.getVersion()}
            Bukkit Version: ${server.bukkitVersion}
            """.trimIndent()
            message(aboutString)
        }
    }
}
