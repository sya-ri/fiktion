plugins {
    kotlin("jvm")
    id("dev.s7a.fiktion") version "0.6.1"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:0.6.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}
