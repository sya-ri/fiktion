package dev.s7a.fiktion

import kotlin.jvm.JvmName

/**
 * Configured generation rule for values of [T].
 */
public interface GenerationSpec<T>

/**
 * Sets a deterministic seed for this generation rule.
 */
public infix fun <T> GenerationSpec<T>.withSeed(seed: Long): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Sets the priority used when multiple generation rules can match.
 */
public infix fun <T> GenerationSpec<T>.withPriority(priority: Int): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Allows this rule to generate `null` with [probability].
 */
public infix fun <T> GenerationSpec<T>.orNullAt(probability: Double): GenerationSpec<T?> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Allows this rule to generate `null` with [probability].
 */
public infix fun <T> GenerationSpec<T>.orNullAt(probability: Probability): GenerationSpec<T?> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Allows this rule to generate the property's default value with [probability].
 */
public infix fun <T> GenerationSpec<T>.orDefaultAt(probability: Double): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Allows this rule to generate the property's default value with [probability].
 */
public infix fun <T> GenerationSpec<T>.orDefaultAt(probability: Probability): GenerationSpec<T> =
    throw NotImplementedError("Rule configuration is not implemented yet.")

/**
 * Sets the generated collection size.
 */
public infix fun <Element, CollectionType : Collection<Element>> GenerationSpec<CollectionType>.withSize(
    size: Int,
): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

/**
 * Sets the generated collection size range.
 */
public infix fun <Element, CollectionType : Collection<Element>> GenerationSpec<CollectionType>.withSize(
    range: IntRange,
): GenerationSpec<CollectionType> = throw NotImplementedError("Collection generation is not implemented yet.")

/**
 * Sets the generated map size.
 */
@JvmName("withMapSize")
public infix fun <Key, Value, MapType : Map<Key, Value>> GenerationSpec<MapType>.withSize(size: Int): GenerationSpec<MapType> =
    throw NotImplementedError("Map generation is not implemented yet.")

/**
 * Sets the generated map size range.
 */
@JvmName("withMapSizeRange")
public infix fun <Key, Value, MapType : Map<Key, Value>> GenerationSpec<MapType>.withSize(range: IntRange): GenerationSpec<MapType> =
    throw NotImplementedError("Map generation is not implemented yet.")
