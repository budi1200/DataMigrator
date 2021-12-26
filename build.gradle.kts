plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.1"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "si.budimir"
version = "1.2"

val kotlinVersion = "1.6.10"
val serializationVersion = "1.3.1"
val okHttpVersion = "4.9.3"
val configurateVersion = "4.1.2"
val ktormVersion = "3.4.1"
val jdbcVersion = "3.36.0.2"
val jodaTimeVersion = "2.10.13"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")

    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")

    implementation("org.ktorm:ktorm-core:${ktormVersion}")
    implementation("org.ktorm:ktorm-support-sqlite:${ktormVersion}")
    implementation("org.xerial:sqlite-jdbc:$jdbcVersion")
    implementation("joda-time:joda-time:$jodaTimeVersion")

    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:$configurateVersion")
    implementation("org.spongepowered:configurate-extra-kotlin:$configurateVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.processResources {
    expand(
        "version" to project.version,
        "kotlinVersion" to kotlinVersion,
        "serializationVersion" to serializationVersion,
        "okHttpVersion" to okHttpVersion
    )
}

tasks.shadowJar {
    // This makes it shadow only stuff with "implementation"
    project.configurations.implementation.get().isCanBeResolved = true
    configurations = mutableListOf(project.configurations.implementation.get()) as List<FileCollection>?

    relocate("org.spongepowered", "si.budimir.dataMigrator.libs.org.spongepowered")
    relocate("net.kyori.adventure.text.minimessage", "si.budimir.dataMigrator.libs.net.kyori.adventure.text.minimessage")
    relocate("org.ktorm", "si.budimir.dataMigrator.libs.org.ktorm")
    relocate("org.sqlite", "si.budimir.dataMigrator.libs.sqlite")
    relocate("joda-time", "si.budimir.dataMigrator.libs.joda-time")
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