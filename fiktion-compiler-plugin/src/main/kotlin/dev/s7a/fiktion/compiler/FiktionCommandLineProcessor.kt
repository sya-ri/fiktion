package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

/**
 * Command line processor for the Fiktion compiler plugin.
 */
@OptIn(ExperimentalCompilerApi::class)
public class FiktionCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = FiktionCompilerPlugin.ID

    override val pluginOptions: Collection<AbstractCliOption> =
        listOf(
            CliOption(
                optionName = ENABLED_OPTION,
                valueDescription = "true|false",
                description = "Enable Fiktion generated metadata registration for this compilation.",
                required = false,
                allowMultipleOccurrences = false,
            ),
        )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        when (option.optionName) {
            ENABLED_OPTION -> configuration.put(FiktionCompilerConfiguration.enabled, value.toBooleanStrict())
            else -> error("Unknown Fiktion compiler plugin option: ${option.optionName}")
        }
    }
}

/**
 * Command line option controlling whether generated metadata is emitted.
 */
private const val ENABLED_OPTION = "enabled"
