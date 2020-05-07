package `in`.kyle.mcspring.command

interface Resolver {
    operator fun invoke(parameter: Class<*>): Any?
}
