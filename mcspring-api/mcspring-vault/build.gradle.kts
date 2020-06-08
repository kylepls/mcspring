plugins {
    id("org.jetbrains.dokka")
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    api(project(":mcspring-api:mcspring-base"))
    api("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}
