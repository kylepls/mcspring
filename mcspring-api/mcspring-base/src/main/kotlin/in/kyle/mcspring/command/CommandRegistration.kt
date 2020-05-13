package `in`.kyle.mcspring.command

import java.lang.reflect.Method

interface CommandRegistration {
    fun register(command: Command, method: Method, obj: Any)
}
