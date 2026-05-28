package dev.s7a.fiktion

/**
 * Runtime description of a generated property.
 */
public data class FakeProperty(
    /**
     * Type that owns this property.
     */
    public val owner: FakeType,
    /**
     * Property name.
     */
    public val name: String,
    /**
     * Property type.
     */
    public val type: FakeType,
    /**
     * Property nullability.
     */
    public val nullability: FakeNullability,
)
