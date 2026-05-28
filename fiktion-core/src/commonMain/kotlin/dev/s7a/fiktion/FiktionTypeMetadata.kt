package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime metadata for a type that Fiktion can generate automatically.
 */
@ExperimentalFiktionApi
public sealed interface FiktionTypeMetadata<T> {
    /**
     * Type represented by this metadata.
     */
    public val type: KType
}
