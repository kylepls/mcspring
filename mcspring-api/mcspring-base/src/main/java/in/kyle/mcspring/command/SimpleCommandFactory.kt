package `in`.kyle.mcspring.command

import `in`.kyle.mcspring.command.BukkitCommandRegistration.CommandFactory
import lombok.RequiredArgsConstructor
import lombok.SneakyThrows
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.stream.Collectors

@Component
@ConditionalOnBean(Plugin::class)
internal open class SimpleCommandFactory : CommandFactory {
    private val methodInjection: SimpleMethodInjection? = null
    private val commandResolvers: Set<CommandResolver>? = null
    private val plugin: Plugin? = null

    override fun makeCommand(method: Method, `object`: Any, name: String): Command {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        val command = constructor.newInstance(name, plugin)
        val executor = makeExecutor(method, `object`)
        command.setExecutor(executor)
        return command
    }

    fun makeExecutor(method: Method, `object`: Any?): CommandExecutor {
        return CommandExecutor { commandSender: CommandSender, bukkitCommand: Command?, label: String?, args: Array<String?>? ->
            try {
//                val command = CommandResolver.Command(commandSender, args, label)
//                val contextResolvers = commandResolvers!!.stream()
//                        .map { r: CommandResolver -> r.makeResolver(command) }
//                        .collect(Collectors.toList())
                TODO()
//                val result: Any = methodInjection.invoke(method,
//                        `object`,
//                        contextResolvers,
//                        commandSender,
//                        args,
//                        label)
//                if (result != null) {
//                    commandSender.sendMessage(result.toString())
//                }
            } catch (exception: RuntimeException) {
                throw RuntimeException("Could not invoke method " + method.name,
                        exception)
            }
            true
        }
    }
}
