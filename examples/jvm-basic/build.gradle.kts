plugins {
    kotlin("jvm") version "2.4.0"
    id("dev.s7a.fiktion") version "0.6.3"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:0.6.3")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}
