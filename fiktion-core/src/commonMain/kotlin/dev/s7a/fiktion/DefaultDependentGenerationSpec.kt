package dev.s7a.fiktion

/**
 * Generation spec whose generator receives previously generated object property values.
 */
internal class DefaultDependentGenerationSpec<T>(
    override val key: RuleKey,
    override val matcher: RuleMatcher,
    val dependencies: List<DependentProperty>,
    val dependentGenerator: FakeContext.(List<Any?>) -> T,
    override var seed: Long? = null,
    override var nullProbability: Probability? = null,
    override var defaultProbability: Probability? = null,
    override val precedence: RulePrecedence = RulePrecedence.GLOBAL,
) : DefaultGenerationSpec<T>(
        key = key,
        matcher = matcher,
        seed = seed,
        nullProbability = nullProbability,
        defaultProbability = defaultProbability,
        precedence = precedence,
    ) {
    /**
     * Generates a value from resolved dependency [values].
     */
    fun generate(
        context: FakeContext,
        values: List<Any?>,
    ): T = context.dependentGenerator(values)

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<T> =
        DefaultDependentGenerationSpec(
            key = key,
            matcher = matcher,
            dependencies = dependencies,
            dependentGenerator = dependentGenerator,
            seed = seed,
            nullProbability = nullProbability,
            defaultProbability = defaultProbability,
            precedence = precedence,
        )
}
