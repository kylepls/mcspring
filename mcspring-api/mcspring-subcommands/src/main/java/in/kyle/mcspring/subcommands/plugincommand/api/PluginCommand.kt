package `in`.kyle.mcspring.subcommands.plugincommand.api

import `in`.kyle.mcspring.subcommands.plugincommand.javacompat.HighIQExecutors
import org.bukkit.command.CommandSender
import kotlin.reflect.KFunction

typealias Err = () -> String
typealias Err1 = (String) -> String

const val defaultStageName = "UNKNOWN"

interface PluginCommand {

    val sender: CommandSender

    fun otherwise(e: KFunction<Any>)

    fun <A> otherwise(e: HighIQExecutors.O0)

    fun <A> otherwise(e: HighIQExecutors.O1<A>)

    fun <A, B> otherwise(e: HighIQExecutors.O2<A, B>)

    fun <A, B, C> otherwise(e: HighIQExecutors.O3<A, B, C>)

    fun <A, B, C, D> otherwise(e: HighIQExecutors.O4<A, B, C, D>)

    fun <A, B, C, D, E> otherwise(e: HighIQExecutors.O5<A, B, C, D, E>)

    fun <A, B, C, D, E, F> otherwise(e: HighIQExecutors.O6<A, B, C, D, E, F>)

    fun otherwise(message: String)

    fun <A> otherwise(e: HighIQExecutors.E1<A>)

    fun <A, B> otherwise(e: HighIQExecutors.E2<A, B>)

    fun <A, B, C> otherwise(e: HighIQExecutors.E3<A, B, C>)

    fun <A, B, C, D> otherwise(e: HighIQExecutors.E4<A, B, C, D>)

    fun <A, B, C, D, E> otherwise(e: HighIQExecutors.E5<A, B, C, D, E>)

    fun <A, B, C, D, E, F> otherwise(e: HighIQExecutors.E6<A, B, C, D, E, F>)

    fun on(command: String, e: KFunction<Any>)

    fun on(command: String, e: HighIQExecutors.O0)

    fun <A> on(command: String, e: HighIQExecutors.O1<A>)

    fun <A, B> on(command: String, e: HighIQExecutors.O2<A, B>)

    fun <A, B, C> on(command: String, e: HighIQExecutors.O3<A, B, C>)

    fun <A, B, C, D> on(command: String, e: HighIQExecutors.O4<A, B, C, D>)

    fun <A, B, C, D, E> on(command: String, e: HighIQExecutors.O5<A, B, C, D, E>)

    fun <A, B, C, D, E, F> on(command: String, e: HighIQExecutors.O6<A, B, C, D, E, F>)

    fun <A> on(command: String, e: HighIQExecutors.E1<A>)

    fun <A, B> on(command: String, e: HighIQExecutors.E2<A, B>)

    fun <A, B, C> on(command: String, e: HighIQExecutors.E3<A, B, C>)

    fun <A, B, C, D> on(command: String, e: HighIQExecutors.E4<A, B, C, D>)

    fun <A, B, C, D, E> on(command: String, e: HighIQExecutors.E5<A, B, C, D, E>)

    fun <A, B, C, D, E, F> on(command: String, e: HighIQExecutors.E6<A, B, C, D, E, F>)

    fun onInvalid(errorMessage: Err1)

    fun then(e: KFunction<Any>)

    fun <A> then(e: HighIQExecutors.O0)

    fun <A> then(e: HighIQExecutors.O1<A>)

    fun <A, B> then(e: HighIQExecutors.O2<A, B>)

    fun <A, B, C> then(e: HighIQExecutors.O3<A, B, C>)

    fun <A, B, C, D> then(e: HighIQExecutors.O4<A, B, C, D>)

    fun <A, B, C, D, E> then(e: HighIQExecutors.O5<A, B, C, D, E>)

    fun <A, B, C, D, E, F> then(e: HighIQExecutors.O6<A, B, C, D, E, F>)

    fun <A> then(e: HighIQExecutors.E1<A>)

    fun <A, B> then(e: HighIQExecutors.E2<A, B>)

    fun <A, B, C> then(e: HighIQExecutors.E3<A, B, C>)

    fun <A, B, C, D> then(e: HighIQExecutors.E4<A, B, C, D>)

    fun <A, B, C, D, E> then(e: HighIQExecutors.E5<A, B, C, D, E>)

    fun <A, B, C, D, E, F> then(e: HighIQExecutors.E6<A, B, C, D, E, F>)

    fun requires(predicate: Boolean, errorMessage: Err)

    fun requiresPlayerSender(errorMessage: Err)

    fun requiresConsoleSender(errorMessage: Err)

    fun requiresPermission(permission: String, errorMessage: Err)

    fun requiresOp(errorMessage: Err)

    fun with(processor: (String) -> Any?, errorMessage: Err1 = { "" }, stageName: String = defaultStageName)

    fun with(processor: (String) -> Any?, errorMessage: Err1 = { "" })
             = with(processor, errorMessage, defaultStageName)

    fun withString()

    fun withSentence()

    fun withInt(errorMessage: Err1)

    fun withInt(errorMessage: String)

    fun withDouble(errorMessage: Err1)

    fun withDouble(errorMessage: String)

    fun withOfflinePlayer(errorMessage: Err1)

    fun withPlayer(errorMessage: Err1)

    fun withPlayer(errorMessage: String)

    fun withWorld(errorMessage: Err1)

    fun withWorld(errorMessage: String)

    fun withXYZInt(errorMessage: Err1)

    fun withXYZInt(errorMessage: String)

    fun <T> withMap(options: Map<String, T>, errorMessage: Err1, stageName: String = defaultStageName)

    fun <T> withMap(options: Map<String, T>, errorMessage: Err1)
            = withMap(options, errorMessage, defaultStageName)

    fun <T> withMap(options: Map<String, T>, errorMessage: String)

    fun withAny(options: Collection<String>, errorMessage: Err1, stageName: String = defaultStageName)

    fun withAny(options: Collection<String>, errorMessage: Err1)
            = withAny(options, errorMessage, defaultStageName)

    fun withAny(options: Collection<String>, errorMessage: String)

}
