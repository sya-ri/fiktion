package dev.s7a.fiktion

/**
 * Target selected by an untyped property dependency rule declaration.
 */
public class DependentRuleTarget<T> internal constructor(
    internal val target: DefaultDependentRuleTarget<T>,
) {
    /**
     * Generates values for this target by invoking [generator] with dependency values in declaration order.
     */
    public infix fun generatesBy(generator: FakeContext.(List<Any?>) -> T): GenerationSpec<T> = target.generatesBy(generator)
}

/**
 * Default mutable dependent rule target implementation.
 */
internal class DefaultDependentRuleTarget<T>(
    private val config: MutableFiktionConfig,
    private val key: RuleKey,
    private val matcher: RuleMatcher,
    private val dependencies: List<DependentProperty>,
) {
    /**
     * Registers [generator] for this target.
     */
    fun generatesBy(generator: FakeContext.(List<Any?>) -> T): DefaultGenerationSpec<T> {
        val spec =
            DefaultDependentGenerationSpec(
                key = key,
                matcher = matcher,
                dependencies = dependencies,
                dependentGenerator = generator,
            )
        config.add(spec)
        return spec
    }
}
