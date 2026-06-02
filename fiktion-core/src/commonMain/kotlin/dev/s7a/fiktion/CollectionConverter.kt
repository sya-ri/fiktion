package dev.s7a.fiktion

/**
 * Builds a concrete collection type from generated elements.
 */
internal data class CollectionConverter(
    /**
     * Type classifier key used for replacement within the same layer.
     */
    val classifier: Any?,
    /**
     * Converts generated elements into the final collection.
     */
    val convert: (List<Any?>) -> Any,
    /**
     * Whether this collection requires distinct generated elements.
     */
    val unique: Boolean = false,
)

/**
 * Builds a concrete map type from generated entries.
 */
internal data class MapConverter(
    /**
     * Type classifier key used for replacement within the same layer.
     */
    val classifier: Any?,
    /**
     * Converts generated entries into the final map.
     */
    val convert: (List<Pair<Any?, Any?>>) -> Any,
)
