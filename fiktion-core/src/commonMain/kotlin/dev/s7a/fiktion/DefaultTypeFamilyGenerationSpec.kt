package dev.s7a.fiktion

/**
 * Mutable generation spec registered for a type family.
 */
internal class DefaultTypeFamilyGenerationSpec<T>(
    override val key: RuleKey,
    override val matcher: RuleMatcher,
    private val typeFamilyGenerator: TypeFamilyGenerator<T>,
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
     * Generates a value from [context].
     */
    fun generate(context: TypeFamilyGenerationContext): T = context.typeFamilyGenerator()

    override fun snapshot(precedence: RulePrecedence): DefaultGenerationSpec<T> =
        DefaultTypeFamilyGenerationSpec(
            key = key,
            matcher = matcher,
            typeFamilyGenerator = typeFamilyGenerator,
            seed = seed,
            nullProbability = nullProbability,
            defaultProbability = defaultProbability,
            precedence = precedence,
        )
}
