pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "fiktion"

include(
    ":fiktion-core",
    ":fiktion-runtime",
    ":fiktion-compiler-plugin",
    ":fiktion-gradle-plugin",
)
