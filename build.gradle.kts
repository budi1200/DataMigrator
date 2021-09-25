plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "1.5.31"
}

group = "si.budimir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0-RC")
    compileOnly("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
    }
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