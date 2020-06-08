plugins {
    id("org.jetbrains.dokka")
    id("com.eden.orchidPlugin")
}

repositories {
    jcenter()
}

dependencies {
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidDocs:0.20.0")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidSourceDoc:0.20.0")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidKotlindoc:0.20.0")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidPluginDocs:0.20.0")
}

tasks.withType(org.jetbrains.dokka.gradle.DokkaTask::class) {
    outputFormat = "html"
    configuration {
        externalDocumentationLink {
            url = uri("https://hub.spigotmc.org/javadocs/bukkit/").toURL()
            url = uri("http://milkbowl.github.io/VaultAPI/").toURL()
        }
    }
}

orchid {
    theme = "Editorial"
}
