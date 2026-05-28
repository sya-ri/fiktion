plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(kotlin("compiler-embeddable"))
}

kotlin {
    jvmToolchain(25)
}
