plugins {
    kotlin("jvm") version "1.3.72"
    id("in.kyle.mcspring") version "0.1.0" apply false
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "1.8"
    }
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

        testImplementation("org.spigotmc:spigot-api:$spigotVersion")
        testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
