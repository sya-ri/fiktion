package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Runtime construction metadata for a value-like type backed by one generated value.
 */
@ExperimentalFiktionApi
public class FiktionValueMetadata<T>(
    /**
     * Type represented by this metadata.
     */
    override val type: KType,
    /**
     * Type used to generate the underlying value.
     */
    public val underlyingType: KType,
    /**
     * Constructor property name for the underlying value.
     */
    public val propertyName: String,
    /**
     * Creates an instance from the generated underlying [value].
     */
    private val constructor: (value: Any?) -> T,
) : FiktionTypeMetadata<T> {
    /**
     * Creates an instance from the generated underlying [value].
     */
    public fun construct(value: Any?): T = constructor(value)
}
