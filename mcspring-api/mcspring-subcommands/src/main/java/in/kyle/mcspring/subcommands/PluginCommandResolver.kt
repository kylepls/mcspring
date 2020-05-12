package `in`.kyle.mcspring.subcommands

import `in`.kyle.mcspring.command.CommandResolver
import `in`.kyle.mcspring.command.ParameterResolver
import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import `in`.kyle.mcspring.subcommands.plugincommand.impl.PluginCommandImpl
import org.springframework.stereotype.Component

@Component
internal class PluginCommandResolver(
        private val injection: SimpleMethodInjection
) : CommandResolver {

    override fun makeResolver(command: CommandResolver.Command): ParameterResolver {
        return object : ParameterResolver {
            override fun resolve(parameter: Class<*>): Any? {
                return if (parameter.isAssignableFrom(PluginCommand::class.java)) {
                    return makeCommand(command)
                } else null
            }
        }
    }

    private fun makeCommand(command: CommandResolver.Command): PluginCommandImpl {
        return PluginCommandImpl(injection, command.sender, command.args.toMutableList(), true)
    }
}
