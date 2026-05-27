package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines value generation.
 */
public interface MapValueSpec<MapType, Key, Value> : GenerationSpec<MapType>

/**
 * Completes this map rule by generating keys with [generator].
 */
public infix fun <MapType, Key, Value> MapValueSpec<MapType, Key, Value>.andKeys(
    generator: Generator<Key>,
): MapEntrySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")
