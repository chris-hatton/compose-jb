pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "web-getting-started-webapp"

include("shared") // Transport models shared between client and server
include("client") // Compose for Web front-end
include("server") // Ktor-based back-end
