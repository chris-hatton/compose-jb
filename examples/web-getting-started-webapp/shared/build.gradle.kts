
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version Versions.SERIALIZATION_PLUGIN
}

kotlin {
    jvm()
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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_RUNTIME}")
                implementation("io.ktor:ktor-client-json:${Versions.KTOR}")
                implementation("io.ktor:ktor-client-serialization:${Versions.KTOR}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
