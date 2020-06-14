plugins {
    `kotlin-dsl`
}

configure<KotlinDslPluginOptions> {
    experimentalWarning.set(false)
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    implementation("io.spring.gradle:dependency-management-plugin:1.0.9.RELEASE")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
    implementation("gradle.plugin.com.eden:orchidPlugin:0.20.0") {
        repositories {
            maven("https://plugins.gradle.org/m2/")
        }
    }

    implementation("io.github.classgraph:classgraph:4.8.83")
    implementation("org.yaml:snakeyaml:1.26")
    implementation("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.3.1.RELEASE")

    testImplementation(gradleTestKit())
}

gradlePlugin {
    plugins {
        register("mcspring-gradle-plugin") {
            id = "in.kyle.mcspring"
            implementationClass = "$id.BuildPlugin"
        }
    }
}
