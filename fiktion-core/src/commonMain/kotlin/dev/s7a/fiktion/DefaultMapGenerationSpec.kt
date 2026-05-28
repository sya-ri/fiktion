package dev.s7a.fiktion

import kotlin.reflect.KType

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
    /**
     * Precedence layer assigned while composing configurations.
     */
    override val precedence: RulePrecedence = RulePrecedence.GLOBAL,
) : DefaultGenerationSpec<MapType>(
        key = key,
        matcher = matcher,
        precedence = precedence,
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
     * Type used to automatically generate keys when no explicit key generator is configured.
     */
    var keyType: KType? = null

    /**
     * Type used to automatically generate values when no explicit value generator is configured.
     */
    var valueType: KType? = null

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

    override fun withSize(size: Int): MapGenerationSpec<Key, Value, MapType> {
        requireFiktionConfiguration(size >= 0) { "Map size must be 0 or greater, but was $size." }
        sizeRange = size..size
        return this
    }

    override fun withSize(range: IntRange): MapGenerationSpec<Key, Value, MapType> {
        requireFiktionConfiguration(!range.isEmpty()) { "Map size range must not be empty." }
        requireFiktionConfiguration(range.first >= 0) { "Map size range must start at 0 or greater, but was $range." }
        sizeRange = range
        return this
    }

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<MapType> =
        DefaultMapGenerationSpec<Key, Value, MapType>(
            key = key,
            matcher = matcher,
            precedence = precedence,
        ).also { spec ->
            spec.sizeRange = sizeRange
            spec.keyType = keyType
            spec.valueType = valueType
            spec.entryGenerator = entryGenerator
            spec.keyGenerator = keyGenerator
            spec.valueGenerator = valueGenerator
            spec.seed = seed
            spec.nullProbability = nullProbability
            spec.defaultProbability = defaultProbability
        }
}

/**
 * Default generated map size range.
 */
private val DEFAULT_MAP_SIZE_RANGE: IntRange = 1..3
