package `in`.kyle.mcspring.command

import `in`.kyle.mcspring.command.BukkitCommandRegistration.CommandFactory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
@ConditionalOnBean(Plugin::class)
open class SimpleCommandFactory(
        private val injection: SimpleMethodInjection,
        private val commandResolvers: Set<CommandResolver>,
        private val plugin: Plugin
) : CommandFactory {

    override fun makeCommand(method: Method, obj: Any, name: String): Command {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        val command = constructor.newInstance(name, plugin)
        val executor = makeExecutor(method, obj)
        command.setExecutor(executor)
        return command
    }

    private fun makeExecutor(method: Method, obj: Any): CommandExecutor {
        return CommandExecutor { commandSender: CommandSender, bukkitCommand: Command, label: String, args: Array<String> ->
            try {
                val command = CommandResolver.Command(commandSender, args.toList(), label)
                val injectionResolvers =
                        injection.makeResolvers(listOf(commandSender, args, label, bukkitCommand))
                val contextParameterResolvers =
                        commandResolvers.map { it.makeResolver(command) }.plus(injectionResolvers)

                val types = method.parameterTypes.toList()
                val parameters = injection.getParameters(types, contextParameterResolvers)
                val result = method.invoke(obj, *parameters)
                if (result !is Unit) {
                    commandSender.sendMessage(result.toString())
                }
            } catch (exception: RuntimeException) {
                throw RuntimeException("Could not invoke method " + method.name,
                        exception)
            }
            true
        }
    }
}
