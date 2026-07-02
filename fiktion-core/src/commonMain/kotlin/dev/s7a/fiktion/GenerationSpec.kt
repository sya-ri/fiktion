package dev.s7a.fiktion

/**
 * Configured generation rule for values of [T].
 */
public sealed interface GenerationSpec<out T> {
    /**
     * Sets a deterministic seed for this generation rule.
     */
    public infix fun withSeed(seed: Long): GenerationSpec<T>

    /**
     * Allows this rule to generate `null` with [probability].
     */
    public infix fun orNullAt(probability: Double): GenerationSpec<T?> = orNullAt(Probability(probability))

    /**
     * Allows this rule to generate `null` with [probability].
     */
    public infix fun orNullAt(probability: Probability): GenerationSpec<T?>

    /**
     * Records the probability of using the property's default value.
     *
     * Default-value generation is applied by the object-construction path and is not used by the current primitive
     * runtime path.
     */
    public infix fun orDefaultAt(probability: Double): GenerationSpec<T> = orDefaultAt(Probability(probability))

    /**
     * Records the probability of using the property's default value.
     *
     * Default-value generation is applied by the object-construction path and is not used by the current primitive
     * runtime path.
     */
    public infix fun orDefaultAt(probability: Probability): GenerationSpec<T>

    /**
     * Excludes [value] from selection candidates for this generation rule.
     *
     * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
     * excluded.
     */
    public infix fun excluding(value: @UnsafeVariance T): GenerationSpec<T>

    /**
     * Excludes [values] from selection candidates for this generation rule.
     *
     * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
     * excluded.
     */
    public infix fun excluding(values: Iterable<@UnsafeVariance T>): GenerationSpec<T>

    /**
     * Excludes value candidates matching [predicate] from this generation rule.
     *
     * Multiple exclusions are cumulative. Generation fails with [FiktionConfigurationException] if all candidates are
     * excluded.
     */
    public infix fun excluding(predicate: (@UnsafeVariance T) -> Boolean): GenerationSpec<T>
}
