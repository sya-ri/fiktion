package dev.s7a.fiktion

/**
 * Mutable collection generation configuration.
 */
internal class DefaultCollectionGenerationSpec<Element, CollectionType : Collection<Element>>(
    /**
     * Stable key used for replacement within the same layer.
     */
    override val key: RuleKey,
    /**
     * Matcher used during generation.
     */
    override val matcher: RuleMatcher,
    /**
     * Element generator used for each generated collection element.
     */
    private val elementGenerator: FakeContext.() -> Element,
    /**
     * Generated collection size range.
     */
    var sizeRange: IntRange = DEFAULT_COLLECTION_SIZE_RANGE,
) : DefaultGenerationSpec<CollectionType>(
        key = key,
        matcher = matcher,
    ),
    CollectionGenerationSpec<Element, CollectionType> {
    override val generator: FakeContext.() -> CollectionType = {
        generateCollection(sizeRange = sizeRange, elementGenerator = elementGenerator)
    }

    override fun withSize(size: Int): CollectionGenerationSpec<Element, CollectionType> {
        requireFiktionConfiguration(size >= 0) { "Collection size must be 0 or greater, but was $size." }
        sizeRange = size..size
        return this
    }

    override fun withSize(range: IntRange): CollectionGenerationSpec<Element, CollectionType> {
        requireFiktionConfiguration(!range.isEmpty()) { "Collection size range must not be empty." }
        requireFiktionConfiguration(range.first >= 0) { "Collection size range must start at 0 or greater, but was $range." }
        sizeRange = range
        return this
    }

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<CollectionType> =
        DefaultCollectionGenerationSpec<Element, CollectionType>(
            key = key,
            matcher = matcher,
            elementGenerator = elementGenerator,
            sizeRange = sizeRange,
        ).also { spec ->
            spec.seed = seed
            spec.nullProbability = nullProbability
            spec.defaultProbability = defaultProbability
        }.let { spec ->
            DefaultGenerationSpec(
                key = spec.key,
                matcher = spec.matcher,
                generator = spec.generator,
                seed = spec.seed,
                nullProbability = spec.nullProbability,
                defaultProbability = spec.defaultProbability,
                precedence = precedence,
            )
        }
}
