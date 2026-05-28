package dev.s7a.fiktion.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/**
 * Gradle plugin that wires the Fiktion compiler plugin into Kotlin compilations.
 */
public class FiktionGradlePlugin :
    Plugin<Project>,
    KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create("fiktion", FiktionExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.fiktionExtension().isEnabledFor(kotlinCompilation.defaultSourceSet.name)

    override fun getCompilerPluginId(): String = "dev.s7a.fiktion"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "dev.s7a",
            artifactId = "fiktion-compiler-plugin",
            version = "0.1.0",
        )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
        kotlinCompilation.target.project.provider {
            listOf(SubpluginOption(key = "enabled", value = "true"))
        }

    /**
     * Returns the Fiktion extension registered on the compilation project.
     */
    private fun KotlinCompilation<*>.fiktionExtension(): FiktionExtension =
        target.project.extensions.getByType(FiktionExtension::class.java)
}
