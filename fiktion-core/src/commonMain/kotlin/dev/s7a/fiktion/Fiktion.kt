package dev.s7a.fiktion

/**
 * Configured fake data generator.
 */
public interface Fiktion {
    /**
     * Entry point for creating and configuring Fiktion instances.
     */
    public companion object {
        /**
         * Creates an isolated Fiktion instance.
         */
        public operator fun invoke(configure: FiktionBuilder.() -> Unit = {}): Fiktion =
            throw NotImplementedError("Fiktion runtime is not implemented yet.")

        /**
         * Applies changes to the global Fiktion configuration and returns a snapshot that can restore the previous configuration.
         */
        public fun configure(configure: FiktionBuilder.() -> Unit): FiktionSnapshot =
            throw NotImplementedError("Global Fiktion configuration is not implemented yet.")
    }
}
