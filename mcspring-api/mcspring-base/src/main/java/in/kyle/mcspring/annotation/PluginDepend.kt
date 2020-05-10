package `in`.kyle.mcspring.annotation

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class PluginDepend(val plugins: Array<String>, val soft: Boolean = false)
