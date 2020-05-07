package `in`.kyle.mcspring.subcommands.plugincommand.javacompat

import java.io.Serializable
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Method

interface HighIQExecutors {

    @JvmDefault
    fun getMethod(e: HighIQExecutors): Method {
        val argCount = this::class.java.declaredMethods.find { it.name == "handle" }!!.parameterCount
        val writeReplace = this::class.java.getDeclaredMethod("writeReplace")
        writeReplace.isAccessible = true
        val sl = writeReplace.invoke(e) as SerializedLambda
        val methodName = sl.implMethodName
        val clazz = Class.forName(sl.implClass.replace("/", "."))

        return clazz.methods.plus(clazz.declaredMethods)
                .filter { it.name == methodName }.first { it.parameters.size == argCount }
                ?: throw RuntimeException("Method not found")
    }

    interface E1<A> : HighIQExecutors, Serializable {
        fun handle(a1: A)
    }

    interface E2<A, B> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B)
    }

    interface E3<A, B, C> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C)
    }

    interface E4<A, B, C, D> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C, d: D)
    }

    interface E5<A, B, C, D, E> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C, d: D, e: E)
    }

    interface E6<A, B, C, D, E, F> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C, d: D, e: E, f: F)
    }

    interface O0 : HighIQExecutors, Serializable {
        fun handle(): Any?
    }

    interface O1<A> : HighIQExecutors, Serializable {
        fun handle(a1: A): Any?
    }

    interface O2<A, B> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B): Any?
    }

    interface O3<A, B, C> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C): Any?
    }

    interface O4<A, B, C, D> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C, d: D): Any?
    }

    interface O5<A, B, C, D, E> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C, d: D, e: E): Any?
    }

    interface O6<A, B, C, D, E, F> : HighIQExecutors, Serializable {
        fun handle(a: A, b: B, c: C, d: D, e: E, f: F): Any?
    }
}
