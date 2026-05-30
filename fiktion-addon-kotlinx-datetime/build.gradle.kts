import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvm()
    js {
        browser {
            testTask {
                failOnNoDiscoveredTests = false
            }
        }
        nodejs()
    }
    wasmJs {
        browser()
        nodejs()
    }
    linuxX64()
    macosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":fiktion-core"))
                api(libs.kotlinx.datetime)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "fiktion-addon-kotlinx-datetime", version.toString())
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
        ),
    )
    pom {
        name.set("fiktion-addon-kotlinx-datetime")
        description.set("Fiktion add-on rules for kotlinx-datetime types.")
        inceptionYear.set("2026")
        url.set("https://github.com/sya-ri/fiktion")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/sya-ri/fiktion/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("sya-ri")
                name.set("sya-ri")
                email.set("contact@s7a.dev")
            }
        }
        scm {
            url.set("https://github.com/sya-ri/fiktion")
        }
    }
}
