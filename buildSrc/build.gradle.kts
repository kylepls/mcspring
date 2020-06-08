plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    implementation("io.spring.gradle:dependency-management-plugin:1.0.9.RELEASE")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.3.0.RELEASE")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
    implementation("gradle.plugin.com.eden:orchidPlugin:0.20.0") {
        repositories {
            maven("https://plugins.gradle.org/m2/")
        }
    }
}
