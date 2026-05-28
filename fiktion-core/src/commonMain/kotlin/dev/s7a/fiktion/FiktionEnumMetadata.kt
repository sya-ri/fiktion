package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime construction metadata for an enum type.
 */
@ExperimentalFiktionApi
public class FiktionEnumMetadata<T : Enum<T>>(
    /**
     * Type represented by this metadata.
     */
    override val type: KType,
    /**
     * Enum entries that can be selected during generation.
     */
    public val entries: List<T>,
) : FiktionTypeMetadata<T>
