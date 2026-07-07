plugins {
    kotlin("jvm")
    id("dev.s7a.fiktion") version "0.6.2"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:0.6.2")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}
