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
    ":fiktion-core",
    ":fiktion-compiler-plugin",
    ":fiktion-gradle-plugin",
)
