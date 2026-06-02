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
    api(project(":fiktion-core"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "fiktion-addon-java", version.toString())
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"),
            sourcesJar = SourcesJar.Sources(),
        ),
    )
    pom {
        name.set("fiktion-addon-java")
        description.set("Fiktion add-on rules for Java standard library types.")
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
