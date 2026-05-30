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
    ":fiktion-addon-java",
    ":fiktion-addon-kotlinx-datetime",
    ":fiktion-core",
    ":fiktion-compiler-plugin",
    ":fiktion-gradle-plugin",
)
