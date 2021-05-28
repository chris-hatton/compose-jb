import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val ktorVersion = "1.5.4"
val serializationVersion = "1.2.1"
val coroutinesVersion = "1.4.32"

plugins {
    id("org.gretty") // Applied first in root project due to: https://github.com/akhikhl/gretty/issues/454
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.32"
    application
    war
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {

                /**
                 * The shared project module contains Transport Models and Constants which
                 * must be the same between Client and Server.
                 */
                implementation(project(":shared"))

                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-server-servlet:$ktorVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")
                implementation("io.ktor:ktor-websockets:$ktorVersion")
                implementation("io.ktor:ktor-server-jetty:$ktorVersion")
            }
        }
    }
}

war {
    webAppDirName = "webapp"
}

gretty {
    contextPath = "/"
}

val jvmJar: Jar by tasks
val jsBrowserProductionWebpack: KotlinWebpack = tasks.findByPath(":client:jsBrowserProductionWebpack") as KotlinWebpack
val war: War by tasks

var jsFileDir: File = jsBrowserProductionWebpack.destinationDirectory
var jsFileName: String = jsBrowserProductionWebpack.outputFileName
var jsFileMapName: String = "$jsFileName.map"

val jsFile = File(jsFileDir, jsFileName)
val jsFileMap = File(jsFileDir, jsFileMapName)

println("JS Client will be transpiled to: '${jsFile.absolutePath}'")

war.apply {
    // Before packaging the WAR file...
    dependsOn(
        jsBrowserProductionWebpack, // ...the JavaScript Client must be built...
        jvmJar // ...and the Server-source must be compiled.
    )

    webInf {
        from("src/jvmMain/resources")

        // `classes` is the folder from static content will be served
        into("classes") {
            from(jsFile) // Package the Client Web-App JavaScript file
            from(jsFileMap) // Package the Kotlin source map; this will be served to the browser to assist with debugging
        }
    }

    group = "application"

    // Include the class-paths for...
    classpath(
        configurations["jvmRuntimeClasspath"], // ...dependencies.
        jvmJar // ...compiled server.
    )
}
