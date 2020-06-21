import com.jfrog.bintray.gradle.BintrayExtension
import java.sql.Date

plugins {
    kotlin("jvm")
    `maven-publish`
    id("com.jfrog.bintray")
}

tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
    dependsOn(tasks.classes)
}

val artifactName = project.name
val artifactGroup = project.group.toString()
val artifactVersion = project.version.toString()

val pomUrl = "https://github.com/kylepls/mcspring"
val pomScmUrl = "https://github.com/kylepls/mcspring"
val pomIssueUrl = "https://github.com/kylepls/mcspring/issues"
val pomDesc = "https://github.com/kylepls/mcspring"

val githubRepo = "kylepls/mcspring"
val githubReadme = "README.md"

val pomLicenseName = "MIT"
val pomLicenseUrl = "https://opensource.org/licenses/mit-license.php"
val pomLicenseDist = "repo"

val pomDeveloperId = "kylepls"
val pomDeveloperName = "Kyle"

publishing {
    publications {
        create<MavenPublication>("mcspring") {
            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion
            from(components["java"])
            artifact(tasks.findByName("sourcesJar"))

            pom.withXml {
                asNode().apply {
                    appendNode("description", pomDesc)
                    appendNode("name", rootProject.name)
                    appendNode("url", pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", pomLicenseName)
                        appendNode("url", pomLicenseUrl)
                        appendNode("distribution", pomLicenseDist)
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", pomDeveloperId)
                        appendNode("name", pomDeveloperName)
                    }
                    appendNode("scm").apply {
                        appendNode("url", pomScmUrl)
                    }
                }
            }
        }
    }
}

bintray {
    user = project.findProperty("bintray.user")?.toString() ?: System.getProperty("bintray.user")
    key = project.findProperty("bintray.key")?.toString() ?: System.getProperty("bintray.key")
    setPublications("mcspring")
    override = true
    publish = true

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "mcspring"
        userOrg = "mcspring"
        setLabels("kotlin", "spigot", "spring")
        vcsUrl = "https://github.com/kylepls/mcspring"
    })
}

tasks.findByName("build")!!.dependsOn(tasks.findByName("publishToMavenLocal"))

