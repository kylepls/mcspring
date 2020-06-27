plugins {
    kotlin("jvm") version "1.3.72"
    id("in.kyle.mcspring") version "0.0.2" apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "in.kyle.mcspring")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        val spigotVersion = "1.15.2-R0.1-SNAPSHOT"
        compileOnly("org.spigotmc:spigot-api:$spigotVersion")
        implementation(kotlin("stdlib"))
    }
}
