package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * Command line processor for the Fiktion compiler plugin.
 */
@OptIn(ExperimentalCompilerApi::class)
public class FiktionCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = FiktionCompilerPlugin.ID

    override val pluginOptions: Collection<AbstractCliOption> = emptyList()

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        error("Unknown Fiktion compiler plugin option: ${option.optionName}")
    }
}
