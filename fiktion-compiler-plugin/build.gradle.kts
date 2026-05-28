plugins {
    alias(libs.plugins.kotlin.jvm)
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
