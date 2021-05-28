
plugins {

    /**
     * See: https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl
     */
    kotlin("multiplatform") version "1.4.32" apply false

    /**
     * The ['Gretty' Gradle plugin](https://github.com/gretty-gradle-plugin) provides tasks to easily package & run/debug a WAR file.
     *
     * Ideally, we would add Gretty only to the server project-module.
     * It is only applied here, to the root project, as a workaround for:
     * https://github.com/akhikhl/gretty/issues/454
     */
    id("org.gretty") version "3.0.4"
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://plugins.gradle.org/m2/")
    }
}
