package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DefaultGenerationSpecTest {
    @Test
    fun `withSeed stores the seed and returns the same spec`() {
        val spec = stringSpec()

        val result = spec withSeed 123

        assertSame(spec, result)
        assertEquals(123, spec.seed)
    }

    @Test
    fun `orNullAt stores a probability value and returns the same spec`() {
        val spec = stringSpec()

        val result = spec orNullAt 30.percent

        assertSame(spec, result)
        assertEquals(30.percent, spec.nullProbability)
    }

    @Test
    fun `orDefaultAt stores a probability value and returns the same spec`() {
        val spec = stringSpec()

        val result = spec orDefaultAt 40.percent

        assertSame(spec, result)
        assertEquals(40.percent, spec.defaultProbability)
    }

    @Test
    fun `snapshot returns a detached copy`() {
        val spec = stringSpec()
        spec withSeed 123
        val snapshot = spec.snapshot()

        spec withSeed 456

        assertNotSame(spec, snapshot)
        assertEquals(123, snapshot.seed)
        assertEquals(456, spec.seed)
    }

    @Test
    fun `snapshot preserves automatic generation`() {
        val spec =
            DefaultGenerationSpec<String>(
                key = RuleKey.Type(typeOf<String>()),
                matcher = RuleMatcher.Type(typeOf<String>()),
                automaticallyGenerates = true,
            )

        val snapshot = spec.snapshot()

        assertTrue(snapshot.automaticallyGenerates)
        assertFalse(stringSpec().snapshot().automaticallyGenerates)
    }

    /**
     * Creates a string generation spec for tests.
     */
    private fun stringSpec(): DefaultGenerationSpec<String> =
        DefaultGenerationSpec(
            key = RuleKey.Type(typeOf<String>()),
            matcher = RuleMatcher.Type(typeOf<String>()),
            generator = { "value" },
        )
}
