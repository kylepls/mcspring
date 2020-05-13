dependencies {
    compile(project(":mcspring-api:mcspring-base"))
    compile("org.springframework.boot:spring-boot-test")
    compile("org.springframework.boot:spring-boot-starter-test")
    compile("org.assertj:assertj-core:3.15.0")
    compile("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    compileOnly("org.spigotmc:spigot-api")
}
