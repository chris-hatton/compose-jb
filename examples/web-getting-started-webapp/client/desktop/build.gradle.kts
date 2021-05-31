
import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.4.0-build179"
    application
}

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven(url = "https://plugins.gradle.org/m2/")
}

version = "1.0"

dependencies {
    implementation(project(":shared"))
    implementation(project(":client"))

    implementation(compose.desktop.currentOs)

    implementation("io.ktor:ktor-client-java:${Versions.KTOR}")
    implementation("io.ktor:ktor-client-serialization:${Versions.KTOR}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_RUNTIME}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}")
    implementation("io.ktor:ktor-client-websockets:${Versions.KTOR}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

