import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management")
    kotlin("jvm")
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

dependencyManagement {
    dependencies {
        dependency("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT") {
            repositories {
                maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
                maven("https://oss.sonatype.org/content/repositories/snapshots")
            }
        }
    }
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api")

    implementation(kotlin("stdlib"))

    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("org.amshove.kluent:kluent:1.61")
    testImplementation("org.mockito:mockito-core:2.+")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = "1.8"
}
