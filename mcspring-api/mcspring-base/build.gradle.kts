plugins {
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

dependencies {
//    compileOnly("org.apache.logging.log4j:log4j-core:2.12.1")
    api(platform("org.springframework.boot:spring-boot-dependencies:2.3.1.RELEASE"))
    api("org.springframework.boot:spring-boot-loader") {
        exclude("org.springframework.boot", "spring-boot-dependencies")
    }
    api("org.springframework.boot:spring-boot-starter") {
//        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("org.springframework.boot", "spring-boot-dependencies")
        exclude("org.yaml", "snakeyaml")
    }
}
