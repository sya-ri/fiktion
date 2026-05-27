plugins {
    kotlin("multiplatform") version "2.3.21" apply false
    kotlin("jvm") version "2.3.21" apply false
    id("org.jmailen.kotlinter") version "5.4.2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}

group = "dev.s7a"
version = "0.1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "io.gitlab.arturbosch.detekt")
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "io.gitlab.arturbosch.detekt")
    }
}
