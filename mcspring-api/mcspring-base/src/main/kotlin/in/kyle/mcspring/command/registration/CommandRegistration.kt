package `in`.kyle.mcspring.command.registration

import `in`.kyle.mcspring.command.Command
import java.lang.reflect.Method

interface CommandRegistration {
    fun register(command: Command, method: Method, obj: Any)
}
