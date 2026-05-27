package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines key generation.
 */
public interface MapKeySpec<MapType, Key, Value> : GenerationSpec<MapType>

/**
 * Completes this map rule by generating values with [generator].
 */
public infix fun <MapType, Key, Value> MapKeySpec<MapType, Key, Value>.andValues(
    generator: Generator<Value>,
): MapEntrySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")
