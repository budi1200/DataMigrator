plugins {
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "si.budimir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
}

tasks.processResources {
    expand("version" to project.version)
}

tasks.shadowJar {
    // This makes it shadow only stuff with "implementation"
    project.configurations.implementation.get().isCanBeResolved = true
    configurations = mutableListOf(project.configurations.implementation.get())

    minimize {}
}

task("buildAndPush") {
    dependsOn("shadowJar")

    doLast {
        copy {
            from("build/libs/DataMigrator-" + project.version + "-all.jar")
            into("../00-server/plugins/")
        }
    }
}