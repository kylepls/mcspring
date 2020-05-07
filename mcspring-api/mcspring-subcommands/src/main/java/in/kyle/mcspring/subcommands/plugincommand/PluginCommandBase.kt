package `in`.kyle.mcspring.subcommands.plugincommand

import org.bukkit.command.CommandSender
import kotlin.reflect.KFunction

interface PluginCommandBase {

    val injections: MutableList<Any>
    val parts: MutableList<String>
    val sender: CommandSender

    var state: State

    fun nextPart(): String?

    fun consumePart()

    fun dirtiesState(predicate: Boolean = true,
                     requiredStates: Array<State> = arrayOf(State.CLEAN),
                     resultingState: State = State.SUCCESS,
                     action: () -> Unit)

    fun sendMessage(message: String)

    fun run(function: KFunction<Any>)

    enum class State {
        CLEAN,
        MISSING_ARG,
        SUCCESS
    }
}
