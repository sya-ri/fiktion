package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines key generation.
 */
public interface MapKeySpec<Key, Value, MapType : Map<Key, Value>> : MapGenerationSpec<Key, Value, MapType>

/**
 * Planned API for completing this map rule by generating values with [generator].
 *
 * This is not implemented by the current runtime path.
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> MapKeySpec<Key, Value, MapType>.andValues(
    generator: Generator<Value>,
): MapEntrySpec<Key, Value, MapType> = throw NotImplementedError("Map generation is not implemented yet.")
