package `in`.kyle.mcspring.command

interface ParameterResolver {
    fun resolve(parameter: Class<*>): Any?
}
