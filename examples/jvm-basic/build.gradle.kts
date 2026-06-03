plugins {
    kotlin("jvm") version "2.3.21"
    id("dev.s7a.fiktion") version "0.4.1"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:0.4.1")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}
