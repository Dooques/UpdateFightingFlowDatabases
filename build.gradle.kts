val kotlinVersion = "2.1.0"
val logbackVersion = "1.5.16"

plugins {
    kotlin("jvm") version "2.2.21"
    id("io.ktor.plugin") version "3.3.2"
    kotlin("plugin.serialization") version "2.2.21"

}

kotlin {
    jvmToolchain(21)
}

group = "com.dooques.fightingflow"
version = "0.0.1"

tasks.jar {
    archiveBaseName.set("app")
}

application {
    mainClass = "com.dooques.fightingflow.ApplicationKt"
}

dependencies {
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("org.jetbrains.exposed:exposed-core:0.59.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.59.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
