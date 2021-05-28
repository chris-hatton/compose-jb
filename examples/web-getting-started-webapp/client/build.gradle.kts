
val ktorVersion = "1.5.4"
val serializationVersion = "1.2.1"
val coroutinesVersion = "1.4.3"

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.32"
    id("org.jetbrains.compose") version "0.0.0-web-dev-12"
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

                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("io.ktor:ktor-client-websockets:$ktorVersion")
            }
        }

        val jsMain by getting {
            dependencies {

                // Compose Web
                implementation(compose.web.web)
                implementation(compose.runtime)

                // Ktor HTTP client
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json-js:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")
            }
        }
    }
}
