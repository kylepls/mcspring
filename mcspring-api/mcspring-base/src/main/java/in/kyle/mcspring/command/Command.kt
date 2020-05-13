package `in`.kyle.mcspring.command

/**
 * Defines a command to be handled by a certain method.
 * Values from this annotation will be reflected in the `plugin.yml`.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(
        val value: String,
        val aliases: Array<String> = [],
        val description: String = "",
        val usage: String = "",
        val permission: String = "",
        val permissionMessage: String = ""
)
