dependencies {
    implementation("org.springframework.boot:spring-boot-starter-aop:")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-log4j2:")
    testImplementation("org.springframework.boot:spring-boot-starter-aop:")
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}
