import java.net.URL

val spigotVersion by extra { "1.16.1" }

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.6.2")
    implementation("io.mockk:mockk:1.10.0")
    val paper = urlFile(
            "https://papermc.io/api/v1/paper/$spigotVersion/latest/download",
            "paper-$spigotVersion"
    )
    compileOnly(paper)
    runtimeOnly(paper)
}

fun urlFile(url: String, name: String): ConfigurableFileCollection {
    val file = File("$buildDir/downloads/${name}.jar")
    file.parentFile.mkdirs()
    if (!file.exists()) {
        URL(url).openStream().use { downloadStream ->
            file.outputStream().use { fileOut ->
                downloadStream.copyTo(fileOut)
            }
        }
    }
    return files(file.absolutePath)
}
