package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.KType

/**
 * Runtime construction metadata for one generated object property.
 */
@ExperimentalFiktionApi
public data class FiktionObjectProperty(
    /**
     * Constructor property name.
     */
    public val name: String,
    /**
     * Constructor property type.
     */
    public val type: KType,
    /**
     * Whether the constructor can use a default value for this property.
     */
    public val hasDefault: Boolean = false,
)
