dependencies {
    api("org.springframework.boot:spring-boot-test")
    api("org.springframework.boot:spring-boot-starter-test")
    api(project(":mcspring-api:mcspring-base"))
    implementation("org.springframework.boot:spring-boot-test")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.assertj:assertj-core:3.15.0")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.3.2")
}
