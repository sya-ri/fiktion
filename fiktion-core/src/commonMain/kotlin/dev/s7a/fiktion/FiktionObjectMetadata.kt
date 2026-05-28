package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime construction metadata for a type that Fiktion can generate automatically.
 */
@ExperimentalFiktionApi
public class FiktionObjectMetadata<T>(
    /**
     * Type represented by this metadata.
     */
    override val type: KType,
    /**
     * Constructor properties in invocation order.
     */
    public val properties: List<FiktionObjectProperty>,
    /**
     * Creates an instance from generated constructor argument values.
     */
    private val constructor: (List<FiktionObjectArgument>) -> T,
) : FiktionTypeMetadata<T> {
    /**
     * Creates an instance from generated constructor [arguments].
     */
    public fun construct(arguments: List<FiktionObjectArgument>): T = constructor(arguments)
}
