package org.springframework.boot.loader.mcspring

import org.springframework.boot.loader.JarLauncher
import java.net.URL
import java.net.URLClassLoader

// Packaging is so that it all blends in with the Spring loader
class McSpringLoader : JarLauncher() {

    fun launch(classLoader: ClassLoader?) {
        val activeArchives = classPathArchives
        val addURL = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        addURL.isAccessible = true
        activeArchives.forEach { addURL(classLoader, it.url) }
    }

    override fun getMainClass() = ""
}
