package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines key generation.
 */
public interface MapKeySpec<Key, Value, MapType : Map<Key, Value>> : MapGenerationSpec<Key, Value, MapType>

/**
 * Completes this map rule by generating values with [generator].
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> MapKeySpec<Key, Value, MapType>.andValues(
    generator: Generator<Value>,
): MapEntrySpec<Key, Value, MapType> {
    val spec = this as DefaultMapGenerationSpec<Key, Value, MapType>
    require(spec.valueGenerator == null) { "Map values are already configured for this rule target." }
    spec.valueGenerator = generator
    return spec
}
