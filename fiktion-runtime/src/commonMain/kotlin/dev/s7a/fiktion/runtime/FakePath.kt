package dev.s7a.fiktion.runtime

/**
 * Runtime path to a generated value.
 */
public data class FakePath(
    /**
     * Properties from the root value to the current value.
     */
    public val segments: List<FakeProperty>,
)
