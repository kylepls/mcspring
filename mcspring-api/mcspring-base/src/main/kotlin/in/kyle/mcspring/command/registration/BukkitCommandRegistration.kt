package `in`.kyle.mcspring.command.registration

import `in`.kyle.mcspring.command.Command
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
@ConditionalOnBean(Plugin::class)
internal class BukkitCommandRegistration(
        private val controller: CommandController,
        private val commandExecutorFactory: CommandExecutorFactory
) : CommandRegistration {

    override fun register(command: Command, method: Method, obj: Any) {
        val bukkitCommand = commandExecutorFactory.makeCommand(method, obj, command.value)
        with(bukkitCommand) {
            aliases = command.aliases.toList()
            description = command.description
            usage = command.usage
            permission = command.permission
            permissionMessage = command.permissionMessage
        }
        controller.registerCommand(bukkitCommand)
    }
}
