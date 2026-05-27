package dev.s7a.fiktion

/**
 * Planned configured generation rule for collection values.
 *
 * Collection generation is not implemented by the current runtime path.
 */
public sealed interface CollectionGenerationSpec<Element, CollectionType : Collection<Element>> : GenerationSpec<CollectionType> {
    /**
     * Sets the generated collection size.
     */
    public infix fun withSize(size: Int): CollectionGenerationSpec<Element, CollectionType>

    /**
     * Sets the generated collection size range.
     */
    public infix fun withSize(range: IntRange): CollectionGenerationSpec<Element, CollectionType>
}
