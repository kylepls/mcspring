plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    compile(project(":mcspring-api:mcspring-jar-loader"))
    compile("org.jetbrains.kotlin:kotlin-reflect:1.3.72")
    compile("org.springframework.boot:spring-boot-loader")
    compile("org.springframework.boot:spring-boot-starter")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    compileOnly("org.apache.logging.log4j:log4j-core:2.12.1")
    compileOnly("org.spigotmc:spigot-api")
}
