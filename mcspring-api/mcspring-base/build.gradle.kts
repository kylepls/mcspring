plugins {
    id("org.jetbrains.dokka")
}

dependencies {
//    compileOnly("org.apache.logging.log4j:log4j-core:2.12.1")
    api("org.springframework.boot:spring-boot-loader") {
        exclude("org.springframework.boot", "spring-boot-dependencies")
    }
    api("org.springframework.boot:spring-boot-starter") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("org.springframework.boot", "spring-boot-dependencies")
    }

    implementation("io.github.classgraph:classgraph:4.8.83")
}
