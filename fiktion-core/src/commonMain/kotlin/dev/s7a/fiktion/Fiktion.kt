package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi

/**
 * Configured fake data generator.
 */
public sealed interface Fiktion {
    /**
     * Entry point for creating and configuring Fiktion instances.
     */
    public companion object {
        /**
         * Creates an isolated Fiktion instance.
         */
        public operator fun invoke(configure: FiktionBuilder.() -> Unit = {}): Fiktion {
            val builder = DefaultFiktionBuilder()
            builder.configure()
            return DefaultFiktion(builder.build())
        }

        /**
         * Applies changes to the global Fiktion configuration and returns a snapshot that can restore the previous configuration.
         */
        public fun configure(configure: FiktionConfigureBuilder.() -> Unit): FiktionSnapshot = GlobalFiktion.configure(configure)

        /**
         * Registers compiler-generated type construction [metadata].
         *
         * This entry point is intended for generated code. Generated metadata is not part of user configuration
         * snapshots and is not removed by [FiktionSnapshot.restore].
         */
        @ExperimentalFiktionApi
        public fun <T> registerGeneratedMetadata(metadata: FiktionTypeMetadata<T>) {
            GlobalFiktion.registerGenerated(metadata)
        }
    }
}
