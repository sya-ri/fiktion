package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines value generation.
 */
public interface MapValueSpec<MapType : Map<Key, Value>, Key, Value> : MapGenerationSpec<Key, Value, MapType>

/**
 * Planned API for completing this map rule by generating keys with [generator].
 *
 * This is not implemented by the current runtime path.
 */
public infix fun <MapType : Map<Key, Value>, Key, Value> MapValueSpec<MapType, Key, Value>.andKeys(
    generator: Generator<Key>,
): MapEntrySpec<MapType, Key, Value> = throw NotImplementedError("Map generation is not implemented yet.")
