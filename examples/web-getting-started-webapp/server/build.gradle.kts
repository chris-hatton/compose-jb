import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    id("org.gretty") // Applied first in root project due to: https://github.com/akhikhl/gretty/issues/454
    kotlin("jvm")
    kotlin("plugin.serialization") version Versions.SERIALIZATION_PLUGIN
    war // Enable packaging the server as a WAR file, see: https://ktor.io/docs/war.html
}

dependencies {

    /**
     * The shared project module contains Transport Models and Constants which
     * must be the same between Client and Server.
     */
    implementation(project(":shared"))

    implementation("io.ktor:ktor-serialization:${Versions.KTOR}")
    implementation("io.ktor:ktor-server-servlet:${Versions.KTOR}")
    implementation("io.ktor:ktor-server-core:${Versions.KTOR}")
    implementation("io.ktor:ktor-html-builder:${Versions.KTOR}")
    implementation("io.ktor:ktor-websockets:${Versions.KTOR}")

    // Not strictly essential, but a practical necessity for developing the server
    // See: https://ktor.io/docs/logging.html#add_dependencies
    implementation("ch.qos.logback:logback-classic:${Versions.LOGBACK}")
}

war {
    webAppDirName = "webapp"
}

gretty {
    contextPath = "/"
}

val clientWebpack: KotlinWebpack = tasks.findByPath(":client:browser:browserBrowserProductionWebpack") as KotlinWebpack

var jsFileDir: File = clientWebpack.destinationDirectory
var jsFileName: String = clientWebpack.outputFileName
var jsFileMapName: String = "$jsFileName.map"

val jsFile = File(jsFileDir, jsFileName)
val jsFileMap = File(jsFileDir, jsFileMapName)

val war: War by tasks

war.apply {
    // Before packaging the WAR file the JavaScript Client must be built...
    dependsOn(clientWebpack)

    // ...and copied to the WEB-INF folder of the WAR file, so it can be served as content.
    webInf {
        // `classes` is the folder from static content will be served
        into("classes") {
            from(jsFile) // Copy the Client Web-App JavaScript file
            from(jsFileMap) // Copy the Kotlin source map; this is also exposed to the browser to assist with debugging
        }
    }

    archiveFileName.set("${rootProject.name}.war")
}
