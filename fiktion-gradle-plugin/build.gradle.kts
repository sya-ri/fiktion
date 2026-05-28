plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.java.gradle.plugin)
}

dependencies {
    compileOnly(kotlin("gradle-plugin-api"))
}

kotlin {
    jvmToolchain(25)
}

gradlePlugin {
    plugins {
        create("fiktion") {
            id = "dev.s7a.fiktion"
            implementationClass = "dev.s7a.fiktion.gradle.FiktionGradlePlugin"
        }
    }
}
