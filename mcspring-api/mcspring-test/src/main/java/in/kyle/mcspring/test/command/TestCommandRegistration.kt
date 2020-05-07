package `in`.kyle.mcspring.test.command

import `in`.kyle.mcspring.command.Command
import `in`.kyle.mcspring.command.CommandRegistration
import `in`.kyle.mcspring.command.CommandResolver
import `in`.kyle.mcspring.command.SimpleMethodInjection
import lombok.RequiredArgsConstructor
import org.bukkit.command.CommandSender
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

@Component
@RequiredArgsConstructor
internal class TestCommandRegistration(
        private val injection: SimpleMethodInjection,
        private val commandResolvers: Set<CommandResolver>
) : CommandRegistration {

    private val commandExecutors: MutableMap<String, Executor> = HashMap()

//    override fun register(command: Command, e: KFunction<Any>) {
//        val executor = makeExecutor(e)
//        getAllNames(command).forEach(Consumer { key: String -> commandExecutors[key] = executor })
//    }

    @Suppress("UNCHECKED_CAST")
    override fun register(command: Command, method: Method, `object`: Any) {
        val executor = makeExecutor(method.kotlinFunction as KFunction<Any>)
        getAllNames(command).forEach(Consumer { key: String -> commandExecutors[key] = executor })
    }

    private fun getAllNames(command: Command): List<String> {
        val commands = command.aliases.toMutableList()
        commands.add(command.value)
        return commands
    }

    private fun makeExecutor(e: KFunction<Any>): Executor {
        return object : Executor {
            override fun execute(sender: CommandSender, label: String, args: Array<String>) {
                val temp = CommandResolver.Command(sender, args, label)

                val thisResolvers = injection.makeResolvers(listOf(sender, args, label))
                val contextResolvers = commandResolvers.map { it.makeResolver(temp) }

                val parameters = injection.getParameters(e, contextResolvers.plus(thisResolvers))
                val result = e.call(*parameters)
                if (result != null) {
                    sender.sendMessage(result.toString())
                }
            }
        }
    }

    fun run(command: String, sender: CommandSender, label: String, args: Array<String>) {
        if (command in commandExecutors) {
            commandExecutors[command]!!.execute(sender, label, args)
        } else {
            throw RuntimeException("Command $command is not registered. Make sure to @Import it")
        }
    }

    internal interface Executor {
        fun execute(sender: CommandSender, label: String, args: Array<String>)
    }

}
