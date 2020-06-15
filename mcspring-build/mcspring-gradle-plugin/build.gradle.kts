plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("java-gradle-plugin")
}

group = "in.kyle.mcspring"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("io.github.classgraph:classgraph:4.8.83")
    implementation("org.yaml:snakeyaml:1.26")
    implementation("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.3.1.RELEASE")

    testImplementation(gradleTestKit())
}

pluginBundle {
    website = "https://github.com/kylepls/mcspring"
    vcsUrl = "https://github.com/kylepls/mcspring.git"
    tags = listOf("bukkit", "spring")
}

gradlePlugin {
    plugins {
        register("mcspring-gradle-plugin") {
            id = "in.kyle.mcspring"
            implementationClass = "$id.BuildPlugin"
            displayName = "mcspring gradle plugin"
            description = "Handles the generation of plugin.yml files and applies Spring package formatting"
        }
    }
}
