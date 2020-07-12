import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    val spigotVersion = "1.15.2-R0.1-SNAPSHOT"
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")

    implementation(kotlin("stdlib"))

    testImplementation("org.mockito:mockito-core:2.+")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("org.spigotmc:spigot-api:$spigotVersion")

    val kotestVersion = "4.1.0.RC2"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = "1.8"
}
