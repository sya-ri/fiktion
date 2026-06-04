package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
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
        if (!configuration[FiktionCompilerConfiguration.enabled, true]) return
        IrGenerationExtension.registerExtension(
            FiktionIrGenerationExtension(
                automaticAddons = configuration[FiktionCompilerConfiguration.automaticAddons, emptyList()],
            ),
        )
    }
}
