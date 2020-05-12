package `in`.kyle.mcspring.command

import `in`.kyle.mcspring.util.SpringScanner
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
internal class CommandScanner(
        private val scanner: SpringScanner,
        private val commandRegistration: CommandRegistration
) : ApplicationContextAware {

    private val registeredCommands: MutableSet<Method> = mutableSetOf()

    override fun setApplicationContext(ctx: ApplicationContext) {
        val scan = scanner.scanMethods(Command::class.java)
        scan.filterKeys { it !in registeredCommands }
                .forEach { (key, value) ->
                    val command = key.getAnnotation(Command::class.java)
                    commandRegistration.register(command, key, value)
                    registeredCommands.add(key)
                }
    }
}
