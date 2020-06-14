package `in`.kyle.mcspring.annotation

/**
 * Declares a dependency in the `plugin.yml`
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class SoftPluginDepend(vararg val plugins: String)
