package `in`.kyle.mcspring.annotation

/**
 * Declares a dependency in the `plugin.yml`
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class PluginDepend(vararg val plugins: String, val soft: Boolean = false)
