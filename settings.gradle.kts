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
    ":fiktion-metadata",
    ":fiktion-compiler-plugin",
    ":fiktion-gradle-plugin",
)
