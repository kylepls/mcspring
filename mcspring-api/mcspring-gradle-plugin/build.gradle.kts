plugins {
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.12.0"
    id("java-gradle-plugin")
}

val createClasspathManifest = tasks.create("createClasspathManifest") {
    val outputDir = buildDir.resolve(name)

    inputs.files(sourceSets.main.get().runtimeClasspath)
    outputs.dir(outputDir)
    logger.info("Dir to $outputDir")

    doLast {
        outputDir.mkdirs()
        val target = outputDir.resolve("plugin-classpath.txt")
        target.writeText(sourceSets.main.get().runtimeClasspath.joinToString("\n"))
        logger.info("Wrote to $target")
    }
}

pluginBundle {
    website = "https://github.com/kylepls/mcspring"
    vcsUrl = "https://github.com/kylepls/mcspring.git"
    tags = listOf("bukkit", "spring")
}

gradlePlugin {
    plugins {
        register("mcspring-gradle-plugin") {
            id = "in.kyle.mcspring"
            implementationClass = "$id.BuildPlugin"
            displayName = "mcspring gradle plugin"
            description = "Handles the generation of plugin.yml files and applies Spring package formatting"
        }
    }
}

dependencies {
    implementation("io.github.classgraph:classgraph:4.8.83")
    implementation("org.yaml:snakeyaml:1.26")
    implementation("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.3.1.RELEASE")
    implementation(project(":mcspring-api:mcspring-base"))
    implementation(project(":mcspring-api:mcspring-commands-dsl"))

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    implementation("io.github.classgraph:classgraph:4.8.83")
    testImplementation(project(":mcspring-api:mcspring-base"))
    testImplementation(project(":mcspring-api:mcspring-commands-dsl"))

    val kotestVersion = "4.1.0.RC2"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console-jvm:$kotestVersion")

    testRuntimeOnly(files(createClasspathManifest))
}

tasks.withType<Jar>() {
    manifest {
        attributes("Implementation-Version" to archiveVersion.get())
    }
}
