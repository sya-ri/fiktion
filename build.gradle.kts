import dev.detekt.gradle.extensions.DetektExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
}

group = "dev.s7a"
version = "0.2.0-SNAPSHOT"

dependencies {
    subprojects.forEach { it ->
        dokka(it)
        kover(it)
    }
}

subprojects {
    apply(plugin = "org.jmailen.kotlinter")
    apply(plugin = "dev.detekt")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    group = rootProject.group
    version = rootProject.version

    extensions.configure<DetektExtension>("detekt") {
        config.setFrom(rootProject.files("detekt.yml"))
    }

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        tasks.matching { it.name.endsWith("BrowserTest") }.configureEach {
            onlyIf {
                providers.gradleProperty("fiktion.enableBrowserTests").getOrElse("false").toBoolean()
            }
        }

        extensions.configure<DetektExtension>("detekt") {
            source.from("src")
        }
    }
}
