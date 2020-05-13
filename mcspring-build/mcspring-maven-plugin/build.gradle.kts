dependencies {
    compile("org.yaml:snakeyaml:1.26")
    compile("org.apache.maven:maven-plugin-api:3.3.9")
    compile("org.apache.maven:maven-core:3.3.9")
    compile("org.apache.maven:maven-project:3.0-alpha-2")
    compile(project(":mcspring-api:mcspring-base"))
    testCompile("org.apache.maven.plugin-testing:maven-plugin-testing-harness:3.3.0")
    testCompile("org.apache.maven:maven-compat:3.6.3")
    compileOnly("org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.0")
    compileOnly("org.projectlombok:lombok:1.18.12")
}
