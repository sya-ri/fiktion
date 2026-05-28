package dev.s7a.fiktion

/**
 * Configured map rule that defines key and value generation.
 */
public interface MapEntrySpec<Key, Value, MapType : Map<Key, Value>> : MapGenerationSpec<Key, Value, MapType>
