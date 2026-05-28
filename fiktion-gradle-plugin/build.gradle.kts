import org.gradle.plugin.devel.tasks.PluginUnderTestMetadata

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.java.gradle.plugin)
}

dependencies {
    compileOnly(kotlin("gradle-plugin-api"))
    testImplementation(kotlin("test"))
    testImplementation(gradleTestKit())
}

kotlin {
    jvmToolchain(25)
}

tasks.named<PluginUnderTestMetadata>("pluginUnderTestMetadata") {
    pluginClasspath.from(configurations.compileClasspath)
}

gradlePlugin {
    plugins {
        create("fiktion") {
            id = "dev.s7a.fiktion"
            implementationClass = "dev.s7a.fiktion.gradle.FiktionGradlePlugin"
        }
    }
}
