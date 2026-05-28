package dev.s7a.fiktion.compiler

import org.jetbrains.kotlin.config.CompilerConfigurationKey

/**
 * Compiler configuration keys used by the Fiktion compiler plugin.
 */
internal object FiktionCompilerConfiguration {
    /**
     * Whether Fiktion metadata generation is enabled for the current compilation.
     */
    val enabled: CompilerConfigurationKey<Boolean> = CompilerConfigurationKey.create("whether Fiktion is enabled")

    /**
     * Fiktion add-on object classes to register automatically.
     */
    val automaticAddons: CompilerConfigurationKey<List<String>> =
        CompilerConfigurationKey.create("Fiktion add-ons to register automatically")
}
