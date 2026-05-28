package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * Registers Fiktion compiler extensions.
 */
@OptIn(ExperimentalCompilerApi::class)
public class FiktionCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String = FiktionCompilerPlugin.ID

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        // Extension registration will be added with the first metadata generation step.
    }
}
