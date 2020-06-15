plugins {
    `mcspring-docs`
}

allprojects {
    group = "in.kyle.mcspring"
    version = "0.1.0"
}

subprojects {
    apply(plugin = "mcspring-build")
    apply(plugin = "mcspring-publish")
}
