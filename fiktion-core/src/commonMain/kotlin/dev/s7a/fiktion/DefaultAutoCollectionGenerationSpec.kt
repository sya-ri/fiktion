package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Mutable collection generation configuration that automatically generates each element.
 */
internal class DefaultAutoCollectionGenerationSpec<Element, CollectionType : Collection<Element>>(
    /**
     * Stable key used for replacement within the same layer.
     */
    override val key: RuleKey,
    /**
     * Matcher used during generation.
     */
    override val matcher: RuleMatcher,
    /**
     * Type generated for each collection element.
     */
    override val autoCollectionElementType: KType,
    /**
     * Generated collection size range.
     */
    override var autoCollectionSizeRange: IntRange = DEFAULT_COLLECTION_SIZE_RANGE,
) : DefaultGenerationSpec<CollectionType>(
        key = key,
        matcher = matcher,
        automaticallyGenerates = true,
        autoCollectionElementType = autoCollectionElementType,
        autoCollectionSizeRange = autoCollectionSizeRange,
    ),
    CollectionGenerationSpec<Element, CollectionType> {
    override fun withSize(size: Int): CollectionGenerationSpec<Element, CollectionType> {
        requireFiktionConfiguration(size >= 0) { "Collection size must be 0 or greater, but was $size." }
        autoCollectionSizeRange = size..size
        return this
    }

    override fun withSize(range: IntRange): CollectionGenerationSpec<Element, CollectionType> {
        requireFiktionConfiguration(!range.isEmpty()) { "Collection size range must not be empty." }
        requireFiktionConfiguration(range.first >= 0) { "Collection size range must start at 0 or greater, but was $range." }
        autoCollectionSizeRange = range
        return this
    }

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<CollectionType> =
        DefaultAutoCollectionGenerationSpec<Element, CollectionType>(
            key = key,
            matcher = matcher,
            autoCollectionElementType = autoCollectionElementType,
            autoCollectionSizeRange = autoCollectionSizeRange,
        ).also { spec ->
            spec.seed = seed
            spec.nullProbability = nullProbability
            spec.defaultProbability = defaultProbability
        }.let { spec ->
            DefaultGenerationSpec(
                key = spec.key,
                matcher = spec.matcher,
                seed = spec.seed,
                nullProbability = spec.nullProbability,
                defaultProbability = spec.defaultProbability,
                automaticallyGenerates = spec.automaticallyGenerates,
                autoCollectionElementType = spec.autoCollectionElementType,
                autoCollectionSizeRange = spec.autoCollectionSizeRange,
                precedence = precedence,
            )
        }
}

/**
 * Default generated collection size range.
 */
internal val DEFAULT_COLLECTION_SIZE_RANGE: IntRange = 1..3
