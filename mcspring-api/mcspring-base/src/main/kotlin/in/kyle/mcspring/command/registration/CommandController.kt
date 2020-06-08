package `in`.kyle.mcspring.command.registration

import `in`.kyle.mcspring.common.commands.CommandMapWrapper
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller

@Controller
@ConditionalOnBean(Plugin::class)
internal class CommandController(
        private val plugin: Plugin
) {
    fun registerCommand(command: Command) = CommandMapWrapper.registerCommand(plugin, command)

    @Bean
    fun commandMap() = CommandMapWrapper.commandMap
}
