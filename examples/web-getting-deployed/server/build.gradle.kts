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

println("JS Client will be compiled to: '${jsFile.absolutePath}'")

war.apply {
    dependsOn(jsBrowserProductionWebpack, jvmJar)
    webInf {
        from("src/jvmMain/resources")
        into("classes") {
            from(jsFile) // Package the Client Web-App JavaScript file
            from(jsFileMap) // Package the Kotlin source map; this will be served to the browser to assist with debugging
        }
    }
    group = "application"

    classpath(
        configurations["jvmRuntimeClasspath"], // Classpath of dependencies
        jvmJar // Classpath of the compiled Server
    )
}
