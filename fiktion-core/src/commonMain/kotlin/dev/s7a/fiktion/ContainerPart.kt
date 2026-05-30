package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Identifies a generated part inside a collection or map container.
 */
internal data class ContainerPart(
    /**
     * Container part being generated.
     */
    val kind: Kind,
    /**
     * Container type that owns this generated part.
     */
    val container: KType,
) {
    /**
     * Supported generated container parts.
     */
    enum class Kind {
        /**
         * Element generated for a collection.
         */
        Collection,

        /**
         * Key generated for a map.
         */
        MapKey,

        /**
         * Value generated for a map.
         */
        MapValue,
    }
}
