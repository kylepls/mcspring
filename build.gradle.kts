import org.springframework.boot.gradle.plugin.SpringBootPlugin

allprojects {
    group = "in.kyle.mcspring"
    version = "0.0.9"
}

plugins {
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.springframework.boot") version "2.2.4.RELEASE" apply false
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.eden.orchidPlugin") version "0.20.0"
}

repositories {
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    orchidRuntime("io.github.javaeden.orchid:OrchidDocs:0.20.0")
    orchidRuntime("io.github.javaeden.orchid:OrchidSourceDoc:0.20.0")
    orchidRuntime("io.github.javaeden.orchid:OrchidKotlindoc:0.20.0")
    orchidRuntime("io.github.javaeden.orchid:OrchidPluginDocs:0.20.0")
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")

    repositories {
        jcenter()
        mavenCentral()
        maven { url = uri("http://repo.maven.apache.org/maven2") }
    }

    dependencyManagement {
        dependencies {
            dependency("org.spigotmc:spigot-api:1.15.1-R0.1-SNAPSHOT") {
                repositories {
                    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
                    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
                }
            }
        }
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    dependencies {
        implementation(kotlin("stdlib"))
    }
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
