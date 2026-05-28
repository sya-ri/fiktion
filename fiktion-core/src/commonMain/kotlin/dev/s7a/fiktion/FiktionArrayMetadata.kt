package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime construction metadata for an array type that Fiktion can generate automatically.
 */
@ExperimentalFiktionApi
public class FiktionArrayMetadata<T>(
    /**
     * Array type represented by this metadata.
     */
    override val type: KType,
    /**
     * Type generated for each array element.
     */
    public val elementType: KType,
    /**
     * Creates an array from generated element values.
     */
    private val constructor: (List<Any?>) -> T,
) : FiktionTypeMetadata<T> {
    /**
     * Creates an array from generated [elements].
     */
    public fun construct(elements: List<Any?>): T = constructor(elements)
}
