package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime construction metadata for a type that Fiktion can generate automatically.
 */
@ExperimentalFiktionApi
public class FiktionObjectMetadata<T>(
    /**
     * Type represented by this metadata.
     */
    public val type: KType,
    /**
     * Constructor properties in invocation order.
     */
    public val properties: List<FiktionObjectProperty>,
    /**
     * Creates an instance from generated constructor argument values.
     */
    private val constructor: (List<Any?>) -> T,
) {
    /**
     * Creates an instance from generated constructor argument [values].
     */
    public fun construct(values: List<Any?>): T = constructor(values)
}
