package dev.s7a.fiktion

/**
 * Planned configured map rule that defines key and value generation.
 *
 * Map generation is not implemented by the current runtime path.
 */
public interface MapEntrySpec<MapType : Map<Key, Value>, Key, Value> : MapGenerationSpec<Key, Value, MapType>
