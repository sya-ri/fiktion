package dev.s7a.fiktion.addon.arrow

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import dev.s7a.fiktion.CannotGenerateException
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.UniqueElementStrategy
import dev.s7a.fiktion.addon.arrow.generators.either
import dev.s7a.fiktion.addon.arrow.generators.ior
import dev.s7a.fiktion.addon.arrow.generators.nonEmptyList
import dev.s7a.fiktion.addon.arrow.generators.nonEmptySet
import dev.s7a.fiktion.addon.arrow.generators.option
import dev.s7a.fiktion.element
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ArrowFiktionAddonTest {
    @Test
    fun `arrow add-on rules are inactive until the add-on is installed`() {
        assertFailsWith<CannotGenerateException> {
            fake<Option<Int>>(seed = 123)
        }
    }

    @Test
    fun `installed arrow add-on generates supported types`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
            }

        val nonEmptyList = fiktion.fake<NonEmptyList<Int>>(seed = 123)
        val nonEmptySet = fiktion.fake<NonEmptySet<Int>>(seed = 123)

        fiktion.fake<Option<Int>>(seed = 123)
        fiktion.fake<Either<String, Int>>(seed = 123)
        fiktion.fake<Ior<String, Int>>(seed = 123)
        assertTrue(nonEmptyList.isNotEmpty())
        assertTrue(nonEmptySet.isNotEmpty())
    }

    @Test
    fun `installed arrow add-on generates nested generic values`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
                type<String>() generatesBy { "value-$index" }
            }

        val value = fiktion.fake<Either<NonEmptyList<String>, Option<String>>>(seed = 1)

        when (value) {
            is Either.Left -> {
                assertTrue(value.value.all { it.startsWith("value-") })
            }

            is Either.Right -> {
                value.value.onSome {
                    assertTrue(it.startsWith("value-"))
                }
            }
        }
    }

    @Test
    fun `installed arrow add-on uses core collection size configs`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
                this using FiktionConfig.Collection.size(4)
            }

        assertEquals(4, fiktion.fake<NonEmptyList<Int>>(seed = 123).size)
        assertEquals(4, fiktion.fake<NonEmptySet<Int>>(seed = 123).size)
    }

    @Test
    fun `installed arrow add-on keeps non-empty collections non-empty with zero size config`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
                this using FiktionConfig.Collection.size(0)
            }

        assertEquals(1, fiktion.fake<NonEmptyList<Int>>(seed = 123).size)
        assertEquals(1, fiktion.fake<NonEmptySet<Int>>(seed = 123).size)
    }

    @Test
    fun `installed arrow add-on applies element target rules to non-empty collections`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
                this using FiktionConfig.Collection.size(3)
                type<NonEmptyList<Int>>().element generatesBy { 42 }
                type<NonEmptySet<Int>>().element generatesBy { index + 10 }
            }

        assertEquals(listOf(42, 42, 42), fiktion.fake<NonEmptyList<Int>>(seed = 123).take(3))
        assertEquals(setOf(10, 11, 12), fiktion.fake<NonEmptySet<Int>>(seed = 123).take(3).toSet())
    }

    @Test
    fun `installed arrow add-on retries non-empty set generation when exact unique element generation is configured`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
                type<NonEmptySet<String>> {
                    this using FiktionConfig.Collection.size(3)
                    this using FiktionConfig.Collection.uniqueElementStrategy(UniqueElementStrategy.Exact(maxAttemptsPerElement = 2))
                    element generatesBy { "item-${index / 2}" }
                }
            }

        val value = fiktion.fake<NonEmptySet<String>>(seed = 123)

        assertEquals(setOf("item-0", "item-1", "item-2"), value.toSet())
    }

    @Test
    fun `installed arrow add-on fails exact non-empty set generation when distinct values cannot fill requested size`() {
        val fiktion =
            Fiktion {
                install(ArrowFiktionAddon)
                type<NonEmptySet<Boolean>> {
                    this using FiktionConfig.Collection.size(3)
                    this using FiktionConfig.Collection.uniqueElementStrategy(UniqueElementStrategy.Exact(maxAttemptsPerElement = 2))
                    element generatesBy { index % 2 == 0 }
                }
            }

        assertFailsWith<CannotGenerateException> {
            fiktion.fake<NonEmptySet<Boolean>>(seed = 123)
        }
    }

    @Test
    fun `direct arrow generators create expected shapes`() {
        val fiktion =
            Fiktion {
                type<Option<Int>>() generatesBy {
                    option { 1 }
                }
                type<Either<String, Int>>() generatesBy {
                    either(left = { "left" }, right = { 2 })
                }
                type<Ior<String, Int>>() generatesBy {
                    ior(left = { "left" }, right = { 3 })
                }
                type<NonEmptyList<Int>>() generatesBy {
                    nonEmptyList(size = 2) { index }
                }
                type<NonEmptySet<Int>>() generatesBy {
                    nonEmptySet(size = 2) { index }
                }
            }

        fiktion.fake<Option<Int>>(seed = 123)
        fiktion.fake<Either<String, Int>>(seed = 123)
        fiktion.fake<Ior<String, Int>>(seed = 123)
        assertEquals(listOf(0, 1), fiktion.fake<NonEmptyList<Int>>(seed = 123).toList())
        assertEquals(setOf(0, 1), fiktion.fake<NonEmptySet<Int>>(seed = 123).toSet())
    }

    @Test
    fun `direct arrow generators pass child contexts to generated parts`() {
        val fiktion =
            Fiktion {
                type<Either<Int, Int>>() generatesBy {
                    either(left = { index }, right = { index })
                }
                type<Ior<Int, Int>>() generatesBy {
                    ior(left = { index }, right = { index })
                }
                type<NonEmptyList<Int>>() generatesBy {
                    nonEmptyList(size = 3) { index }
                }
                type<NonEmptySet<Int>>() generatesBy {
                    nonEmptySet(size = 3) { index }
                }
            }

        when (val either = fiktion.fake<Either<Int, Int>>(seed = 123)) {
            is Either.Left -> assertEquals(0, either.value)
            is Either.Right -> assertEquals(1, either.value)
        }
        when (val ior = fiktion.fake<Ior<Int, Int>>(seed = 123)) {
            is Ior.Left -> assertEquals(0, ior.value)
            is Ior.Right -> assertEquals(1, ior.value)
            is Ior.Both -> assertEquals(0 to 1, ior.leftValue to ior.rightValue)
        }
        assertEquals(listOf(0, 1, 2), fiktion.fake<NonEmptyList<Int>>(seed = 123).toList())
        assertEquals(setOf(0, 1, 2), fiktion.fake<NonEmptySet<Int>>(seed = 123).toSet())
    }

    @Test
    fun `direct non-empty set generator retries duplicates when exact unique element generation is configured`() {
        val fiktion =
            Fiktion {
                type<NonEmptySet<Int>> {
                    this using FiktionConfig.Collection.uniqueElementStrategy(UniqueElementStrategy.Exact(maxAttemptsPerElement = 2))
                    this generatesBy {
                        nonEmptySet(size = 3) { index / 2 }
                    }
                }
            }

        assertEquals(setOf(0, 1, 2), fiktion.fake<NonEmptySet<Int>>(seed = 123).toSet())
    }
}
