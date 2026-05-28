import org.gradle.plugin.devel.tasks.PluginUnderTestMetadata

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.plugin.publish)
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
    website.set("https://github.com/sya-ri/fiktion")
    vcsUrl.set("https://github.com/sya-ri/fiktion")
    plugins {
        create("fiktion") {
            id = "dev.s7a.fiktion"
            displayName = "Fiktion Gradle Plugin"
            description = "Kotlin fake data for tests that should read like intent, not fixture setup."
            tags.set(listOf("kotlin", "testing", "fake-data"))
            implementationClass = "dev.s7a.fiktion.gradle.FiktionGradlePlugin"
        }
    }
}
