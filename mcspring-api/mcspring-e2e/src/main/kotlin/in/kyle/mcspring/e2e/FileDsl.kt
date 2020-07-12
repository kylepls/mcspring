package `in`.kyle.mcspring.e2e

import java.io.File

class FileDsl(private val file: File) {

    fun delete(string: String) {
        val toDelete = file.resolve(string)
        if (toDelete.isFile) {
            toDelete.delete()
        } else {
            toDelete.deleteRecursively()
        }
    }

    fun create(string: String) = file.apply { mkdirs() }.resolve(string).createNewFile()

    fun folder(string: String, lambda: FileDsl.() -> Unit) = FileDsl(file.resolve(string)).lambda()

    fun write(string: String) = file.writeText(string)

    operator fun String.unaryPlus() = create(this)

    operator fun String.unaryMinus() = delete(this)

    operator fun String.invoke(lambda: FileDsl.() -> Unit) = folder(this, lambda)
}

operator fun File.invoke(lambda: FileDsl.() -> Unit) = FileDsl(this).lambda()
