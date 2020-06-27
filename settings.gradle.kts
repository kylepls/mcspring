plugins {
    id("com.gradle.enterprise").version("3.3.4")
}

rootProject.name = "mcspring"

include(":mcspring-api")
include(":mcspring-api:mcspring-commands-dsl")
include(":mcspring-api:mcspring-base")
include(":mcspring-api:mcspring-vault")
include(":mcspring-api:mcspring-rx")
include(":mcspring-api:mcspring-guis")
include(":mcspring-api:mcspring-chat")

include(":mcspring-build")
include(":mcspring-build:mcspring-gradle-plugin")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
