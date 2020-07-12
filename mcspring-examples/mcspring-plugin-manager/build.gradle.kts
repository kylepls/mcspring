import `in`.kyle.mcspring.mcspring

dependencies {
    implementation(mcspring("base"))
    implementation(mcspring("commands-dsl"))
    testImplementation(mcspring("e2e"))
}

mcspring {
    pluginAuthor = "kylepls"
    pluginMainPackage = "in.kyle.mcspring.pluginmanager"
}
