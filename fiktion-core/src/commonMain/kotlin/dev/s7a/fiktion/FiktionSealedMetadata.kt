package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime construction metadata for a sealed type.
 */
@ExperimentalFiktionApi
public class FiktionSealedMetadata<T>(
    /**
     * Type represented by this metadata.
     */
    override val type: KType,
    /**
     * Concrete subtypes that can be selected during generation.
     */
    public val subtypes: List<KType>,
) : FiktionTypeMetadata<T>
