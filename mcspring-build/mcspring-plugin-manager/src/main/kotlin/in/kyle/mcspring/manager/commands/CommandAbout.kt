package `in`.kyle.mcspring.manager.commands

import `in`.kyle.mcspring.command.Command
import org.bukkit.Server
import org.springframework.boot.info.BuildProperties
import org.springframework.core.SpringVersion
import org.springframework.stereotype.Component

@Component
internal class CommandAbout {

    @Command(value = "about",
            description = "Provides information about current library versions in use")
    fun about(properties: BuildProperties, server: Server): String = """
        Plugin Name: ${properties.name}
        Plugin Version: ${properties.version}
        Spring Version: ${SpringVersion.getVersion()}
        Bukkit Version: ${server.bukkitVersion}
        """.trimIndent()
}
