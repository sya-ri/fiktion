package dev.s7a.fiktion.metadata

/**
 * Generated metadata for a type that Fiktion can inspect.
 */
public interface FiktionTypeMetadata<T> {
    /**
     * Type represented by this metadata.
     */
    public val type: FiktionTypeRef

    /**
     * Properties available for generated construction.
     */
    public val properties: List<FiktionPropertyMetadata>
}
