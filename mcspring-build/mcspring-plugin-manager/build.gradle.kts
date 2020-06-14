plugins {
    id("in.kyle.mcspring")
}

dependencies {
    implementation(project(":mcspring-api:mcspring-base"))
    implementation(project(":mcspring-api:mcspring-commands-dsl"))
}

mcspring {
    pluginAuthor = "kylepls"
}

tasks.build {
    dependsOn(tasks.named("buildServer"))
}
