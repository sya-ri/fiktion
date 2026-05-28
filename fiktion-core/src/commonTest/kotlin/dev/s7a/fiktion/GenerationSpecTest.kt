package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
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
