package dev.s7a.fiktion

/**
 * Configured generation rule for map values.
 */
public sealed interface MapGenerationSpec<Key, Value, MapType : Map<Key, Value>> : GenerationSpec<MapType> {
    /**
     * Sets the generated map size.
     */
    public infix fun withSize(size: Int): MapGenerationSpec<Key, Value, MapType>

    /**
     * Sets the generated map size range.
     */
    public infix fun withSize(range: IntRange): MapGenerationSpec<Key, Value, MapType>
}
