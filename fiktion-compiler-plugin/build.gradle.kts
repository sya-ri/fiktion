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
    compileOnly(kotlin("compiler-embeddable"))
    testImplementation(kotlin("test"))
    testImplementation(project(":fiktion-core"))
}

kotlin {
    jvmToolchain(25)
}

val compilerPluginJar = tasks.named<Jar>("jar")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (name != "compileTestKotlin") return@configureEach

    dependsOn(compilerPluginJar)
    compilerOptions.freeCompilerArgs.add(
        compilerPluginJar.map { jar ->
            "-Xplugin=${jar.archiveFile.get().asFile.absolutePath}"
        },
    )
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.s7a", "fiktion-compiler-plugin", version.toString())
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationJavadoc"),
            sourcesJar = SourcesJar.Sources(),
        ),
    )
    pom {
        name.set("fiktion-compiler-plugin")
        description.set("Kotlin compiler plugin that generates Fiktion metadata.")
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
