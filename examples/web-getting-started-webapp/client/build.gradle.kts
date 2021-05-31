
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Versions.SERIALIZATION_PLUGIN
}

kotlin {

    js(compiler = IR) {
        useCommonJs()
        browser()
    }

    jvm()

    sourceSets {

        val commonMain by getting {
            dependencies {

                /**
                 * The shared project module contains Transport Models and Constants which
                 * must be the same between Client and Server.
                 */
                implementation(project(":shared"))

                implementation("io.ktor:ktor-client-serialization:${Versions.KTOR}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_RUNTIME}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}")
                implementation("io.ktor:ktor-client-websockets:${Versions.KTOR}")
            }
        }
    }
}
