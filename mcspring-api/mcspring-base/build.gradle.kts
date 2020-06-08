plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    api("org.apache.logging.log4j:log4j-core:2.12.1")
    api("org.spigotmc:spigot-api")
    api("org.springframework.boot:spring-boot-loader")
    api("org.springframework.boot:spring-boot-starter")

    implementation(project(":mcspring-api:mcspring-common"))
    implementation(project(":mcspring-api:mcspring-jar-loader"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.72")
    implementation("org.springframework.boot:spring-boot-loader")
    implementation("org.springframework.boot:spring-boot-starter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.spigotmc:spigot-api")
}
