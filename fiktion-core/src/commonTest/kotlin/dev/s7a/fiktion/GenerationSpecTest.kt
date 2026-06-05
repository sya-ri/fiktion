package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class GenerationSpecTest {
    @Test
    fun `withSeed stores the seed and returns the same spec`() {
        val rule = stringSpec()

        val spec = rule withSeed 123

        assertSame(rule, spec)
        assertEquals(123, rule.seed)
    }

    @Test
    fun `orNullAt stores a probability value and returns the same spec`() {
        val rule = stringSpec()

        val spec = rule orNullAt 30.percent

        assertSame(rule, spec)
        assertEquals(30.percent, rule.nullProbability)
    }

    @Test
    fun `orNullAt accepts a raw probability value`() {
        val rule = stringSpec()

        rule orNullAt 0.3

        assertEquals(Probability(0.3), rule.nullProbability)
    }

    @Test
    fun `double percent converts a percentage to a probability`() {
        assertEquals(Probability(0.125), 12.5.percent)
    }

    @Test
    fun `orDefaultAt stores a probability value and returns the same spec`() {
        val rule = stringSpec()

        val spec = rule orDefaultAt 40.percent

        assertSame(rule, spec)
        assertEquals(40.percent, rule.defaultProbability)
    }

    @Test
    fun `orDefaultAt accepts a raw probability value`() {
        val rule = stringSpec()

        rule orDefaultAt 0.4

        assertEquals(Probability(0.4), rule.defaultProbability)
    }

    @Test
    fun `snapshot keeps values when the original spec changes`() {
        val rule = stringSpec()
        rule withSeed 123
        rule orNullAt 30.percent
        rule orDefaultAt 40.percent

        val snapshot = rule.snapshot()

        rule withSeed 456
        rule orNullAt 50.percent
        rule orDefaultAt 60.percent

        assertNotSame(rule, snapshot)
        assertEquals(123, snapshot.seed)
        assertEquals(30.percent, snapshot.nullProbability)
        assertEquals(40.percent, snapshot.defaultProbability)
    }

    @Test
    fun `mutating a snapshot does not change the original spec`() {
        val rule = stringSpec()
        rule withSeed 123
        rule orNullAt 30.percent
        rule orDefaultAt 40.percent
        val snapshot = rule.snapshot()

        snapshot withSeed 456
        snapshot orNullAt 50.percent
        snapshot orDefaultAt 60.percent

        assertEquals(123, rule.seed)
        assertEquals(30.percent, rule.nullProbability)
        assertEquals(40.percent, rule.defaultProbability)
    }

    /**
     * Creates a string rule for generation spec tests.
     */
    private fun stringSpec(): DefaultGenerationSpec<String> =
        DefaultGenerationSpec(
            key = RuleKey.Type(typeOf<String>()),
            matcher = RuleMatcher.Type(typeOf<String>()),
            generator = { "value" },
        )
}
