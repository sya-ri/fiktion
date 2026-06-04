pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "fiktion"

include(
    ":fiktion-addon-arrow-core",
    ":fiktion-addon-java",
    ":fiktion-addon-kotlinx-datetime",
    ":fiktion-core",
    ":fiktion-compiler-plugin",
    ":fiktion-detekt-rules",
    ":fiktion-gradle-plugin",
)
