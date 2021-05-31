
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.COMPOSE_FOR_WEB
}

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven(url = "https://plugins.gradle.org/m2/")
}

version = "1.0"

kotlin {
    js(name = "browser", compiler = IR) {
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

        val browserMain by getting {
            dependencies {

                /**
                 * The shared project module contains Transport Models and Constants which
                 * must be the same between Client and Server.
                 */
                implementation(project(":shared"))
                implementation(project(":client"))

                // Compose Web
                implementation(compose.web.web)
                implementation(compose.runtime)

                implementation("io.ktor:ktor-client-serialization:${Versions.KTOR}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_RUNTIME}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}")
                implementation("io.ktor:ktor-client-websockets:${Versions.KTOR}")
            }
        }
    }
}
