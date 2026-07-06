package dev.s7a.fiktion

/**
 * Generation spec that selects one value from a fixed candidate list.
 */
internal class DefaultOneOfGenerationSpec<T>(
    override val key: RuleKey,
    override val matcher: RuleMatcher,
    private val values: List<T>,
    override var seed: Long? = null,
    override var nullProbability: Probability? = null,
    override var defaultProbability: Probability? = null,
    override var exclusions: GenerationExclusions = GenerationExclusions(),
    override val precedence: RulePrecedence = RulePrecedence.GLOBAL,
) : DefaultGenerationSpec<T>(
        key = key,
        matcher = matcher,
        seed = seed,
        nullProbability = nullProbability,
        defaultProbability = defaultProbability,
        exclusions = exclusions,
        precedence = precedence,
    ) {
    fun generate(context: FakeContext): T {
        requireNoTypeExclusions(exclusions, "generatesOneOf")
        val candidates = values.filterNot { value -> exclusions.excludesValue(value) }
        requireFiktionConfiguration(candidates.isNotEmpty()) { "values must not be empty after exclusions." }
        return candidates[context.random.nextInt(candidates.size)]
    }

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<T> =
        DefaultOneOfGenerationSpec(
            key = key,
            matcher = matcher,
            values = values,
            seed = seed,
            nullProbability = nullProbability,
            defaultProbability = defaultProbability,
            exclusions = exclusions,
            precedence = precedence,
        )
}
