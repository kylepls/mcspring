package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.command.CommandResolver
import `in`.kyle.mcspring.command.Resolver
import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommand
import lombok.AllArgsConstructor
import org.springframework.stereotype.Component
import java.util.*

@Component
@AllArgsConstructor
internal class PluginCommandResolver(
        private val injection: SimpleMethodInjection
) : CommandResolver {
    override fun makeResolver(command: CommandResolver.Command): Resolver {
        return object : Resolver {
            override fun invoke(parameter: Class<*>): Any? {
                return if (parameter.isAssignableFrom(PluginCommand::class.java)) Optional.of(
                        makeCommand(command)) else Optional.empty<Any>()
            }
        }
    }

    private fun makeCommand(command: CommandResolver.Command): PluginCommand {
        val args = mutableListOf(*command.args)
        return PluginCommand(injection, command.sender, args)
    }
}
