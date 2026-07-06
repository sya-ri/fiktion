import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.tasks.PathSensitivity

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.gradle.plugin.publish) apply false
}

group = "dev.s7a"
version = "0.6.0"

val dokkaOlderVersionsDir = layout.buildDirectory.dir("dokka/olderVersions")

dependencies {
    subprojects.forEach {
        dokka(it)
        kover(it)
    }
    dokkaPlugin(libs.dokka.versioning.plugin)
}

dokka {
    pluginsConfiguration {
        versioning {
            version.set(project.version.toString())
            olderVersionsDir.set(dokkaOlderVersionsDir)
        }
    }
}

val prepareDokkaVersioning by tasks.registering {
    doLast {
        dokkaOlderVersionsDir.get().asFile.mkdirs()
    }
}

tasks.matching { it.name == "dokkaGenerateHtml" || it.name == "dokkaGeneratePublicationHtml" }.configureEach {
    dependsOn(prepareDokkaVersioning)
    inputs.dir(dokkaOlderVersionsDir)
        .withPropertyName("dokkaOlderVersions")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}

subprojects {
    apply(plugin = "org.jmailen.kotlinter")
    apply(plugin = "dev.detekt")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    group = rootProject.group
    version = rootProject.version

    tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
        onlyIf {
            gradle.startParameter.taskNames.none { it.equals("publishToMavenLocal", ignoreCase = true) }
        }
    }

    extensions.configure<DetektExtension>("detekt") {
        config.setFrom(rootProject.files("detekt.yml"))
    }

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<DetektExtension>("detekt") {
            source.from("src")
        }
    }
}
