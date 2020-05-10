package `in`.kyle.mcspring.subcommands.plugincommand.javacompat

import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandBase.State
import `in`.kyle.mcspring.subcommands.plugincommand.PluginCommandExecutors
import `in`.kyle.mcspring.subcommands.plugincommand.api.PluginCommand
import `in`.kyle.mcspring.subcommands.plugincommand.javacompat.HighIQExecutors.*
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Method
import kotlin.reflect.KFunction

@Suppress("UNCHECKED_CAST")
interface PluginCommandExecutorsJavaSupport : PluginCommandExecutors {

    override fun on(command: String, e: O0) = this.on(command, e::handle as KFunction<Any>)

    override fun <A> on(command: String, e: O1<A>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B> on(command: String, e: O2<A, B>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C> on(command: String, e: O3<A, B, C>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C, D> on(command: String, e: O4<A, B, C, D>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E> on(command: String, e: O5<A, B, C, D, E>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E, F> on(command: String, e: O6<A, B, C, D, E, F>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A> on(command: String, e: E1<A>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B> on(command: String, e: E2<A, B>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C> on(command: String, e: E3<A, B, C>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C, D> on(command: String, e: E4<A, B, C, D>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E> on(command: String, e: E5<A, B, C, D, E>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E, F> on(command: String, e: E6<A, B, C, D, E, F>) = this.on(command, e::handle as KFunction<Any>)

    override fun <A> then(e: O0) = this.then(e, e::handle as KFunction<Any>)

    override fun <A> then(e: O1<A>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B> then(e: O2<A, B>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C> then(e: O3<A, B, C>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D> then(e: O4<A, B, C, D>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E> then(e: O5<A, B, C, D, E>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E, F> then(e: O6<A, B, C, D, E, F>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A> then(e: E1<A>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B> then(e: E2<A, B>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C> then(e: E3<A, B, C>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D> then(e: E4<A, B, C, D>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E> then(e: E5<A, B, C, D, E>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E, F> then(e: E6<A, B, C, D, E, F>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A> otherwise(e: O0) = this.then(e, e::handle as KFunction<Any>)

    override fun <A> otherwise(e: O1<A>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B> otherwise(e: O2<A, B>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C> otherwise(e: O3<A, B, C>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D> otherwise(e: O4<A, B, C, D>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E> otherwise(e: O5<A, B, C, D, E>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E, F> otherwise(e: O6<A, B, C, D, E, F>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A> otherwise(e: E1<A>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B> otherwise(e: E2<A, B>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C> otherwise(e: E3<A, B, C>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D> otherwise(e: E4<A, B, C, D>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E> otherwise(e: E5<A, B, C, D, E>) = this.then(e, e::handle as KFunction<Any>)

    override fun <A, B, C, D, E, F> otherwise(e: E6<A, B, C, D, E, F>) = this.then(e, e::handle as KFunction<Any>)

    override fun otherwise(message: String) {
        dirtiesState(requiredStates = arrayOf(State.CLEAN, State.MISSING_ARG)) { sendMessage(message) }
    }

    private fun then(e: HighIQExecutors, function: KFunction<Any>) {
        dirtiesState {
            execute {
                val types: List<Class<*>> = getMethod(e).parameterTypes.toList()
                val sendOutput = types.any { PluginCommand::class.java.isAssignableFrom(it) }
                super.runWithContext(function, types, sendOutput)
            }
        }
    }

    companion object {
        private fun getMethod(e: HighIQExecutors): Method {
            val argCount = e::class.java.declaredMethods.find { it.name == "handle" }!!.parameterCount
            val writeReplace = e::class.java.getDeclaredMethod("writeReplace")
            writeReplace.isAccessible = true
            val sl = writeReplace.invoke(e) as SerializedLambda
            val methodName = sl.implMethodName
            val clazz = Class.forName(sl.implClass.replace("/", "."))

            return clazz.methods.plus(clazz.declaredMethods)
                    .filter { it.name == methodName }.first { it.parameters.size == argCount }
                    ?: throw RuntimeException("Method not found")
        }
    }
}

