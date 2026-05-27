package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.Generator

/**
 * Partially configured map rule that defines value generation.
 */
public interface MapValueSpec<Key, Value, MapType : Map<Key, Value>> : MapGenerationSpec<Key, Value, MapType>

/**
 * Planned API for completing this map rule by generating keys with [generator].
 *
 * This is not implemented by the current runtime path.
 */
public infix fun <Key, Value, MapType : Map<Key, Value>> MapValueSpec<Key, Value, MapType>.andKeys(
    generator: Generator<Key>,
): MapEntrySpec<Key, Value, MapType> = throw NotImplementedError("Map generation is not implemented yet.")
