package `in`.kyle.mcspring.command

import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import org.bukkit.command.Command as BukkitCommand

@Component
@ConditionalOnBean(Plugin::class)
internal class BukkitCommandRegistration(
        private val controller: CommandController,
        private val commandFactory: CommandFactory
) : CommandRegistration {

    override fun register(command: Command, method: Method, obj: Any) {
        val bukkitCommand = commandFactory.makeCommand(method, obj, command.value)
        with(bukkitCommand) {
            aliases = command.aliases.toList()
            description = command.description
            usage = command.usage
            permission = command.permission
            permissionMessage = command.permissionMessage
        }
        controller.registerCommand(bukkitCommand)
    }

    internal interface CommandFactory {
        fun makeCommand(method: Method, obj: Any, name: String): BukkitCommand
    }
}
