package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines key generation.
 */
public interface MapKeySpec<MapType : Map<Key, Value>, Key, Value> : MapGenerationSpec<Key, Value, MapType>

/**
 * Planned API for completing this map rule by generating values with [generator].
 *
 * This is not implemented by the current runtime path.
 */
public infix fun <MapType : Map<Key, Value>, Key, Value> MapKeySpec<MapType, Key, Value>.andValues(
    generator: Generator<Value>,
): MapEntrySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")
