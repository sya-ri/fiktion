import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.maven.publish)
}

dependencies {
    compileOnly(libs.detekt.api)
    testImplementation(kotlin("test"))
    testImplementation(libs.detekt.test)
}

kotlin {
    jvmToolchain(25)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "fiktion-detekt-rules", version.toString())
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"),
            sourcesJar = SourcesJar.Sources(),
        ),
    )
    pom {
        name.set("fiktion-detekt-rules")
        description.set("Detekt rules for keeping Fiktion test DSL usage focused and consistent.")
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
