package `in`.kyle.mcspring.command

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
