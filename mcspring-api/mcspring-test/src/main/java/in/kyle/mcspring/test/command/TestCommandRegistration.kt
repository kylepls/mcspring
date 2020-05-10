package `in`.kyle.mcspring.test.command

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.command.CommandRegistration
import `in`.kyle.mcspring.command.CommandResolver
import `in`.kyle.mcspring.command.SimpleMethodInjection
import org.bukkit.command.CommandSender
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

typealias CommandSig = (CommandSender, String, Array<String>) -> Unit

@Component
internal class TestCommandRegistration(
        private val injection: SimpleMethodInjection,
        private val commandResolvers: Set<CommandResolver>
) : CommandRegistration {

    private val commandExecutors: MutableMap<String, CommandSig> = HashMap()

    override fun register(command: Command, method: Method, obj: Any) {
        @Suppress("UNCHECKED_CAST")
        val executor = makeExecutor(method.kotlinFunction as KFunction<Any>)
        getAllNames(command).forEach(Consumer { key: String -> commandExecutors[key] = executor })
    }

    private fun getAllNames(command: Command): List<String> {
        val commands = command.aliases.toMutableList()
        commands.add(command.value)
        return commands
    }

    private fun makeExecutor(e: KFunction<Any>): CommandSig {
        return { sender: CommandSender, label: String, args: Array<String> ->
            val temp = CommandResolver.Command(sender, args.toList(), label)
            val thisResolvers = injection.makeResolvers(listOf(sender, args, label))
            val contextResolvers = commandResolvers.map { it.makeResolver(temp) }

            val parameters = injection.getParameters(e.javaMethod!!.parameterTypes.toList(),
                    contextResolvers.plus
                    (thisResolvers))
            val result = e.call(*parameters)
            if (result !is Unit) {
                sender.sendMessage(result.toString())
            }
        }
    }

    fun run(command: String, sender: CommandSender, label: String, args: Array<String>) {
        if (command in commandExecutors) {
            commandExecutors[command]!!(sender, label, args)
        } else {
            throw RuntimeException("Command $command is not registered. Make sure to @Import it")
        }
    }
}
