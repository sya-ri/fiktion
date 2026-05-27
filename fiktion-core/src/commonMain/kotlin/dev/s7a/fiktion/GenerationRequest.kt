package dev.s7a.fiktion

import kotlin.reflect.KProperty1
import kotlin.reflect.KType

/**
 * Runtime request for generating one value.
 */
internal data class GenerationRequest(
    /**
     * Type currently being generated.
     */
    val type: KType,
    /**
     * Owner type when generating a property value.
     */
    val owner: KType? = null,
    /**
     * Property name when generating a property value.
     */
    val propertyName: String? = null,
    /**
     * Full property path from the root value for context metadata.
     */
    val propertyPath: List<KProperty1<*, *>> = emptyList(),
    /**
     * Typed path segments used for path rule matching.
     *
     * Object graph generation must provide these segments when it wants typed nested path rules to match. Segment type
     * ids must use the same non-null normalized form as [PropertyPath].
     */
    val pathSegments: List<PathRuleSegment> =
        propertyPath.mapIndexed { index, property ->
            PathRuleSegment(
                ownerId = null,
                name = property.name,
                valueId = if (index == propertyPath.lastIndex) type.nonNullTypeId() else null,
            )
        },
)
