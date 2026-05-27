plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.detekt) apply false
}

group = "dev.s7a"
version = "0.1.0-SNAPSHOT"

subprojects {
    group = rootProject.group
    version = rootProject.version

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "dev.detekt")

        val javaToolchains = extensions.getByType<org.gradle.jvm.toolchain.JavaToolchainService>()
        val java17Home = javaToolchains.launcherFor {
            languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(17))
        }.map { it.metadata.installationPath }

        extensions.configure<dev.detekt.gradle.extensions.DetektExtension>("detekt") {
            source.from(
                "src/commonMain/kotlin",
                "src/commonTest/kotlin",
                "src/jvmMain/kotlin",
                "src/jvmTest/kotlin",
                "src/jsMain/kotlin",
                "src/jsTest/kotlin",
                "src/wasmJsMain/kotlin",
                "src/wasmJsTest/kotlin",
                "src/linuxX64Main/kotlin",
                "src/linuxX64Test/kotlin",
                "src/macosArm64Main/kotlin",
                "src/macosArm64Test/kotlin",
                "src/iosSimulatorArm64Main/kotlin",
                "src/iosSimulatorArm64Test/kotlin",
            )
        }

        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            jdkHome.set(java17Home)
            jvmTarget = "17"
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "org.jmailen.kotlinter")
        apply(plugin = "dev.detekt")

        val javaToolchains = extensions.getByType<org.gradle.jvm.toolchain.JavaToolchainService>()
        val java17Home = javaToolchains.launcherFor {
            languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(17))
        }.map { it.metadata.installationPath }

        tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
            jdkHome.set(java17Home)
            jvmTarget = "17"
        }
    }
}
