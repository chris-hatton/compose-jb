
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Versions.SERIALIZATION_PLUGIN
    id("org.jetbrains.compose") version Versions.COMPOSE_FOR_WEB
}

kotlin {
    js(IR) {
        browser {
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
        binaries.executable()
    }

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

        val jsMain by getting {
            dependencies {

                // Compose Web
                implementation(compose.web.web)
                implementation(compose.runtime)

                // Ktor HTTP client
                implementation("io.ktor:ktor-client-js:${Versions.KTOR}")
                implementation("io.ktor:ktor-client-json-js:${Versions.KTOR}")
                implementation("io.ktor:ktor-client-serialization-js:${Versions.KTOR}")
            }
        }
    }
}
