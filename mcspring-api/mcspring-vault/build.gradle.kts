plugins {
    id("org.jetbrains.dokka")
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compile(project(":mcspring-api:mcspring-base"))
    compileOnly("org.spigotmc:spigot-api")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}
