package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext

/**
 * Mutable map generation configuration.
 */
internal class DefaultMapGenerationSpec<Key, Value, MapType : Map<Key, Value>>(
    /**
     * Stable key used for replacement within the same layer.
     */
    override val key: RuleKey,
    /**
     * Matcher used during generation.
     */
    override val matcher: RuleMatcher,
) : DefaultGenerationSpec<MapType>(
        key = key,
        matcher = matcher,
    ),
    MapGenerationSpec<Key, Value, MapType>,
    MapKeySpec<Key, Value, MapType>,
    MapValueSpec<Key, Value, MapType>,
    MapEntrySpec<Key, Value, MapType> {
    /**
     * Generated map size range.
     */
    var sizeRange: IntRange = DEFAULT_MAP_SIZE_RANGE

    /**
     * Entry generator used when entries are generated as a pair.
     */
    var entryGenerator: (FakeContext.() -> Pair<Key, Value>)? = null

    /**
     * Key generator used when keys and values are generated separately.
     */
    var keyGenerator: (FakeContext.() -> Key)? = null

    /**
     * Value generator used when keys and values are generated separately.
     */
    var valueGenerator: (FakeContext.() -> Value)? = null

    override val generator: FakeContext.() -> MapType = {
        generateMap(this@DefaultMapGenerationSpec)
    }

    override fun withSize(size: Int): MapGenerationSpec<Key, Value, MapType> {
        require(size >= 0) { "Map size must be 0 or greater, but was $size." }
        sizeRange = size..size
        return this
    }

    override fun withSize(range: IntRange): MapGenerationSpec<Key, Value, MapType> {
        require(!range.isEmpty()) { "Map size range must not be empty." }
        require(range.first >= 0) { "Map size range must start at 0 or greater, but was $range." }
        sizeRange = range
        return this
    }

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<MapType> =
        DefaultMapGenerationSpec<Key, Value, MapType>(
            key = key,
            matcher = matcher,
        ).also { spec ->
            spec.sizeRange = sizeRange
            spec.entryGenerator = entryGenerator
            spec.keyGenerator = keyGenerator
            spec.valueGenerator = valueGenerator
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

/**
 * Default generated map size range.
 */
private val DEFAULT_MAP_SIZE_RANGE: IntRange = 1..3
