package dev.s7a.fiktion.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.io.File
import java.util.Properties
import java.util.zip.ZipFile

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
            version = compilerPluginVersion(),
        )

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
        kotlinCompilation.target.project.provider {
            listOf(SubpluginOption(key = "enabled", value = "true")) +
                kotlinCompilation.automaticAddonOptions()
        }

    /**
     * Returns the Fiktion extension registered on the compilation project.
     */
    private fun KotlinCompilation<*>.fiktionExtension(): FiktionExtension =
        target.project.extensions.getByType(FiktionExtension::class.java)

    /**
     * Returns compiler options for add-ons listed in compilation dependencies.
     */
    private fun KotlinCompilation<*>.automaticAddonOptions(): List<SubpluginOption> =
        (compileDependencyFiles.files + runtimeDependencyFiles?.files.orEmpty())
            .flatMap { file -> file.fiktionAddonClassNames() }
            .distinct()
            .map { className -> SubpluginOption(key = "automaticAddon", value = className) }

    /**
     * Returns the compiler plugin version that matches this Gradle plugin artifact.
     */
    private fun compilerPluginVersion(): String =
        requireNotNull(
            FiktionGradlePlugin::class.java
                .getResourceAsStream("/dev/s7a/fiktion/gradle/fiktion-gradle-plugin.properties")
                ?.use { stream ->
                    Properties().apply { load(stream) }.getProperty("version")
                },
        ) {
            "Fiktion Gradle plugin version metadata is missing."
        }
}

/**
 * Fiktion add-on index paths inside dependency archives or class directories.
 */
private const val FIKTION_ADDON_INDEX_PATH: String = "META-INF/fiktion/addons"

private val FIKTION_ADDON_INDEX_PATHS: List<String> =
    listOf(
        FIKTION_ADDON_INDEX_PATH,
        "default/resources/$FIKTION_ADDON_INDEX_PATH",
    )

/**
 * Returns Fiktion add-on class names declared by this dependency file.
 */
internal fun File.fiktionAddonClassNames(): List<String> =
    (
        when {
            isDirectory -> readDirectoryAddonIndex()
            extension in FIKTION_ADDON_INDEX_ARCHIVE_EXTENSIONS -> readArchiveAddonIndex()
            else -> emptyList()
        } + readGradleProjectResourceAddonIndex()
    ).distinct()

private val FIKTION_ADDON_INDEX_ARCHIVE_EXTENSIONS: Set<String> = setOf("jar", "klib")

/**
 * Reads add-on class names from a directory index file.
 */
private fun File.readDirectoryAddonIndex(): List<String> =
    FIKTION_ADDON_INDEX_PATHS
        .firstNotNullOfOrNull { path -> resolve(path).takeIf { file -> file.isFile } }
        .readAddonIndex()

/**
 * Reads add-on class names from a text index file.
 */
private fun File?.readAddonIndex(): List<String> = this?.readLines()?.addonClassNames() ?: emptyList()

/**
 * Reads add-on class names from an archive index entry.
 */
private fun File.readArchiveAddonIndex(): List<String> =
    if (!isFile) {
        emptyList()
    } else {
        ZipFile(this).use { zip ->
            val entry =
                FIKTION_ADDON_INDEX_PATHS
                    .firstNotNullOfOrNull { path -> zip.getEntry(path) }
                    ?: return emptyList()
            zip.getInputStream(entry).bufferedReader().use { reader ->
                reader.readLines().addonClassNames()
            }
        }
    }

private fun File.readGradleProjectResourceAddonIndex(): List<String> =
    gradleProjectResourceAddonIndexCandidates()
        .firstNotNullOfOrNull { file -> file.takeIf { it.isFile } }
        .readAddonIndex()

private fun File.gradleProjectResourceAddonIndexCandidates(): List<File> {
    val path = path.replace(File.separatorChar, '/')
    val classDirectory =
        listOf("/build/classes/kotlin/", "/build/classes/java/")
            .firstNotNullOfOrNull { marker ->
                val markerIndex = path.indexOf(marker)
                if (markerIndex == -1) return@firstNotNullOfOrNull null
                val sourceSet = path.substring(markerIndex + marker.length).substringBefore('/')
                val projectPath = path.substring(0, markerIndex)
                projectPath to sourceSet
            }
    if (classDirectory != null) {
        val (projectPath, sourceSet) = classDirectory
        return listOf(
            File(projectPath, "src/$sourceSet/resources/$FIKTION_ADDON_INDEX_PATH"),
            File(projectPath, "build/resources/$sourceSet/$FIKTION_ADDON_INDEX_PATH"),
        )
    }

    val jarMarker = "/build/libs/"
    val jarMarkerIndex = path.indexOf(jarMarker)
    if (jarMarkerIndex == -1) return emptyList()
    val projectPath = path.substring(0, jarMarkerIndex)
    val sourceSet =
        if (path.substring(jarMarkerIndex + jarMarker.length).contains("test-fixtures")) {
            "testFixtures"
        } else {
            "main"
        }
    return listOf(
        File(projectPath, "src/$sourceSet/resources/$FIKTION_ADDON_INDEX_PATH"),
        File(projectPath, "build/resources/$sourceSet/$FIKTION_ADDON_INDEX_PATH"),
    )
}

/**
 * Parses add-on class names from index lines.
 */
private fun List<String>.addonClassNames(): List<String> =
    map { line -> line.substringBefore('#').trim() }
        .filter { line -> line.isNotEmpty() }
