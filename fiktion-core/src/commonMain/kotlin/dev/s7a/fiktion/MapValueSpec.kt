package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines value generation.
 */
public interface MapValueSpec<Key, Value, MapType : Map<Key, Value>> : MapGenerationSpec<Key, Value, MapType>

/**
 * Completes this map rule by generating keys with [generator].
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> MapValueSpec<Key, Value, MapType>.andKeys(
    generator: Generator<Key>,
): MapEntrySpec<Key, Value, MapType> {
    val spec = this as DefaultMapGenerationSpec<Key, Value, MapType>
    require(spec.keyGenerator == null) { "Map keys are already configured for this rule target." }
    spec.keyGenerator = generator
    return spec
}
