package `in`.kyle.mcspring.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class PluginDepend(vararg val plugins: String, val soft: Boolean = false)
