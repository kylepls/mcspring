package `in`.kyle.mcspring.test.command

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.command.CommandRegistration
import `in`.kyle.mcspring.command.CommandResolver
import `in`.kyle.mcspring.command.SimpleMethodInjection
import org.bukkit.command.CommandSender
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

typealias CommandSig = (CommandSender, String, Array<String>) -> Unit

@Component
class TestCommandRegistration(
        private val injection: SimpleMethodInjection,
        private val commandResolvers: Set<CommandResolver>
) : CommandRegistration {

    private val commandExecutors: MutableMap<String, CommandSig> = HashMap()

    override fun register(command: Command, method: Method, obj: Any) {
        @Suppress("UNCHECKED_CAST")
        val executor = makeExecutor(method.kotlinFunction as KFunction<Any>, obj)
        command.aliases.plus(command.value).forEach { commandExecutors[it] = executor }
    }

    private fun makeExecutor(e: KFunction<Any>, obj: Any): CommandSig {
        return { sender: CommandSender, label: String, args: Array<String> ->
            val temp = CommandResolver.Command(sender, args.toList(), label)
            val miscResolvers = injection.makeResolvers(listOf(sender))
            val contextResolvers = commandResolvers.map { it.makeResolver(temp) }

            val parameterTypes = e.javaMethod!!.parameterTypes.toList()
            val parameters = injection.getParameters(parameterTypes, contextResolvers.plus(miscResolvers))
            val result = e.call(obj, *parameters)
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
