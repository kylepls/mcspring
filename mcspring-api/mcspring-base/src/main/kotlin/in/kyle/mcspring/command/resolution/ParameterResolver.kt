package `in`.kyle.mcspring.command.resolution

interface ParameterResolver {
    fun resolve(parameter: Class<*>): Any?
}
