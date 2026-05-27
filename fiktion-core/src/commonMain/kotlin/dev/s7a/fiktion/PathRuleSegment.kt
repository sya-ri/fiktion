package dev.s7a.fiktion

/**
 * Internal rule identity for one property path segment.
 */
@PublishedApi
internal data class PathRuleSegment(
    /**
     * Non-null normalized type identifier that owns the property, when known.
     */
    val ownerId: String?,
    /**
     * Property name.
     */
    val name: String,
    /**
     * Non-null normalized property value type identifier, when known.
     */
    val valueId: String?,
)
