pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "web-getting-deployed"

include("shared") // Transport models shared between client and server
include("client") // Web front-end
include("server") // Ktor back-end
