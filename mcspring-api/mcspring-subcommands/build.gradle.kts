plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    compile(project(":mcspring-api:mcspring-base"))
    testCompile(project(":mcspring-api:mcspring-test"))
    compileOnly("org.spigotmc:spigot-api")
}
