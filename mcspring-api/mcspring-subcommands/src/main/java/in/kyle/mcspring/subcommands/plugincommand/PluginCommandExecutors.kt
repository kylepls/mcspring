package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.command.SimpleMethodInjection
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.isAccessible

interface PluginCommandExecutors : PluginCommandBase {

    val injection: SimpleMethodInjection

    fun otherwise(e: KFunction<Any>) {
        dirtiesState(requiredStates = arrayOf(PluginCommandBase.State.CLEAN, PluginCommandBase.State.MISSING_ARG)) { run(e) }
    }

    fun on(command: String, e: KFunction<Any>) {
        if (nextPart().equals(command)) {
            dirtiesState { run(e) }
        }
    }

    fun onInvalid(errorMessage: (String) -> String) {
        if (nextPart() != null) {
            dirtiesState {
                sendMessage(errorMessage(parts[0]))
            }
        }
    }

    fun then(e: KFunction<Any>) {
        dirtiesState { run(e) }
    }

    override fun run(function: KFunction<Any>) {
        val contextResolvers = injection.makeResolvers(sender, makeNextExecutor(), *injections.toTypedArray())
        val parameters = injection.getParameters(function, contextResolvers)
        function.isAccessible = true
        val output = function.call(*parameters)
        if (output !is Unit) {
            sendMessage(output.toString())
        }
    }

    fun makeNextExecutor(): PluginCommand? {
        return if (parts.isNotEmpty()) {
            PluginCommand(injection, sender, parts).apply {
                injections.addAll(this.injections)
                parts.removeAt(0)
            }
        } else null
    }
}
