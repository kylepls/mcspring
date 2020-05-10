package `in`.kyle.mcspring.subcommands.plugincommand

import `in`.kyle.mcspring.command.SimpleMethodInjection
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import kotlin.reflect.KFunction
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.javaType

interface PluginCommandExecutors : PluginCommandBase, PluginCommand {

    val injection: SimpleMethodInjection

    override fun otherwise(e: KFunction<Any>) {
        dirtiesState(requiredStates = arrayOf(State.CLEAN, State.MISSING_ARG)) {
            execute { runWithKotlinContext(e) }
        }
    }

    override fun on(command: String, e: KFunction<Any>) {
        addCompletion(command, "on")
        if (nextPart().equals(command)) {
            dirtiesState {
                val receivesPluginCommand = e.parameters.any {
                    it.type.isSubtypeOf(PluginCommand::class.createType())
                }
                if (receivesPluginCommand) {
                    runWithKotlinContext(e, sendOutput = false)
                } else {
                    execute { runWithKotlinContext(e) }
                }
            }
        }
    }

    override fun onInvalid(errorMessage: (String) -> String) {
        if (nextPart() != null) {
            dirtiesState {
                execute { sendMessage(errorMessage(parts[0])) }
            }
        }
    }

    override fun then(e: KFunction<Any>) = dirtiesState { execute { runWithKotlinContext(e) } }

    fun runWithContext(e: KFunction<Any>, types: List<Class<*>>, sendOutput: Boolean = true) {
        val nextExecutor = makeNextExecutor()
        child = nextExecutor
        val out = injection.callWithInjection(e, types, injections.plus(nextExecutor).plus(sender))
        if (out !is Unit && sendOutput) {
            sendMessage(out.toString())
        }
    }

    private fun runWithKotlinContext(e: KFunction<Any>, sendOutput: Boolean = true) {
        val types = e.parameters.map { it.type.javaType as Class<*> }
        return runWithContext(e, types, sendOutput)
    }

    private fun makeNextExecutor(): PluginCommandImpl? {
        return if (parts.isNotEmpty()) {
            PluginCommandImpl(injection, sender, parts, runExecutors).apply {
                injections.addAll(this.injections)
                parts.removeAt(0)
            }
        } else null
    }
}
