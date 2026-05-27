import dev.detekt.gradle.extensions.DetektExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.detekt) apply false
}

group = "dev.s7a"
version = "0.1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "dev.detekt")

        extensions.configure<DetektExtension>("detekt") {
            config.setFrom(rootProject.files("detekt.yml"))
            source.from("src")
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "dev.detekt")

        extensions.configure<DetektExtension>("detekt") {
            config.setFrom(rootProject.files("detekt.yml"))
        }
    }
}
