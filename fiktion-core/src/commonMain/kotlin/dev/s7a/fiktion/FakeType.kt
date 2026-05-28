package dev.s7a.fiktion

/**
 * Runtime description of a generated type.
 */
public data class FakeType(
    /**
     * Stable type identifier.
     */
    public val id: String,
    /**
     * Human-readable type name used in diagnostics.
     */
    public val displayName: String,
) {
    public companion object {
        /**
         * Type metadata was not available on the current generation path.
         */
        public val Unknown: FakeType = FakeType(id = "unknown", displayName = "unknown")
    }
}
