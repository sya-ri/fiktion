package dev.s7a.fiktion

/**
 * Configured generation rule for collection values.
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
