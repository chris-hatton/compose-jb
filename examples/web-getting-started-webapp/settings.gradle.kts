pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "web-getting-started-webapp"

include("server") // Ktor-based back-end

include("shared") // Transport models shared between client and server

include("client") // Code shared between clients e.g. ViewModels (presentation logic)
include(":client:browser") // Compose for Web front-end
include(":client:desktop") // Compose for Desktop front-end
