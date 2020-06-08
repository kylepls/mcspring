package `in`.kyle.mcspring.commands.dsl.mcspring

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.commands.dsl.CommandExecutor
import `in`.kyle.mcspring.commands.dsl.CommandMeta
import `in`.kyle.mcspring.commands.dsl.CommandUtils
import `in`.kyle.mcspring.util.SpringScanner
import org.bukkit.plugin.Plugin
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
open class CommandDslRegistration(
        private val plugin: Plugin,
        private val scanner: SpringScanner
) : ApplicationContextAware {

    private val registeredCommands: MutableSet<Method> = mutableSetOf()

    override fun setApplicationContext(ctx: ApplicationContext) {
        scanner.scanMethods(Command::class.java).filterKeys { it !in registeredCommands }
                .filterKeys { it.returnType == CommandExecutor::class }
                .forEach { (key, value) ->
                    val command = key.getAnnotation(Command::class.java)
                    val meta = CommandMeta().apply {
                        name = command.value
                        description = command.description
                        usageMessage = command.usage
                        permission = command.permission
                        permissionMessage = command.permissionMessage
                        aliases = command.aliases.toMutableList()
                        executor = key.invoke(value) as CommandExecutor
                    }
                    CommandUtils.register(plugin, meta)
                }
        scanner.scanMethods().filterKeys { it !in registeredCommands }
                .filterKeys { it.returnType == CommandMeta::class }
                .forEach { (key, value) ->
                    val meta = key.invoke(value) as CommandMeta
                    CommandUtils.register(plugin, meta)
                }
    }
}
