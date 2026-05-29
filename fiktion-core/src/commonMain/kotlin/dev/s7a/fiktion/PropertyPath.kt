package dev.s7a.fiktion

import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Type-safe path from [Root] to a nested [Value] property.
 *
 * Path matching uses non-null normalized type ids. This lets `/` traverse nullable intermediate properties while
 * matching the object shape rather than the nullability state at a particular step.
 */
public class PropertyPath<Root, out Value>
    @PublishedApi
    internal constructor(
        /**
         * Properties that make up this path.
         */
        public val properties: List<KProperty1<*, *>>,
        /**
         * Type reached by this path.
         */
        @PublishedApi
        internal val valueType: KType,
        /**
         * Typed rule segments for matching this path.
         */
        @PublishedApi
        internal val segments: List<PathRuleSegment>,
    )

/**
 * Creates a nested property path using `/`.
 *
 * Nullable intermediate properties are accepted and normalized to their non-null value type for matching.
 */
@Suppress("DEPRECATION_ERROR")
public inline operator fun <reified Root, reified Intermediate : Any, reified Value> KProperty1<Root, Intermediate?>.div(
    next: KProperty1<Intermediate, Value>,
): PropertyPath<Root, Value> = div(next = next, root = typeOf<Root>(), intermediate = typeOf<Intermediate>(), value = typeOf<Value>())

/**
 * Creates a nested property path using `/`.
 *
 * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
 * and the properties consistent.
 */
@Deprecated("Use the reified property path div overload.", level = DeprecationLevel.ERROR)
public fun <Root, Intermediate : Any, Value> KProperty1<Root, Intermediate?>.div(
    next: KProperty1<Intermediate, Value>,
    root: KType,
    intermediate: KType,
    value: KType,
): PropertyPath<Root, Value> =
    PropertyPath(
        properties = listOf(this, next),
        valueType = value,
        segments =
            listOf(
                PathRuleSegment(
                    ownerId = root.nonNullTypeId(),
                    name = name,
                    valueId = intermediate.nonNullTypeId(),
                ),
                PathRuleSegment(
                    ownerId = intermediate.nonNullTypeId(),
                    name = next.name,
                    valueId = value.nonNullTypeId(),
                ),
            ),
    )

/**
 * Appends [next] to this property path.
 *
 * Nullable intermediate properties are accepted and normalized to their non-null value type for matching.
 */
@Suppress("DEPRECATION_ERROR")
public inline operator fun <Root, reified Intermediate : Any, reified Value> PropertyPath<Root, Intermediate?>.div(
    next: KProperty1<Intermediate, Value>,
): PropertyPath<Root, Value> = div(next = next, intermediate = typeOf<Intermediate>(), value = typeOf<Value>())

/**
 * Appends [next] to this property path.
 *
 * This low-level overload is intended for callers that already carry [KType] values. The caller must keep the types
 * and the properties consistent.
 */
@Deprecated("Use the reified property path div overload.", level = DeprecationLevel.ERROR)
public fun <Root, Intermediate : Any, Value> PropertyPath<Root, Intermediate?>.div(
    next: KProperty1<Intermediate, Value>,
    intermediate: KType,
    value: KType,
): PropertyPath<Root, Value> =
    PropertyPath(
        properties = properties + next,
        valueType = value,
        segments =
            segments +
                PathRuleSegment(
                    ownerId = intermediate.nonNullTypeId(),
                    name = next.name,
                    valueId = value.nonNullTypeId(),
                ),
    )

/**
 * Returns the collection element type for this property path.
 */
internal fun PropertyPath<*, *>.collectionElementType(): KType =
    valueType.arguments.firstOrNull()?.type
        ?: throw FiktionConfigurationException("Cannot infer collection element type for property path.")

/**
 * Returns the map key type for this property path.
 */
internal fun PropertyPath<*, *>.mapKeyType(): KType =
    valueType.arguments.getOrNull(0)?.type
        ?: throw FiktionConfigurationException("Cannot infer map key type for property path.")

/**
 * Returns the map value type for this property path.
 */
internal fun PropertyPath<*, *>.mapValueType(): KType =
    valueType.arguments.getOrNull(1)?.type
        ?: throw FiktionConfigurationException("Cannot infer map value type for property path.")
