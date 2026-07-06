@file:OptIn(ExperimentalFiktionApi::class, ExperimentalTime::class)

package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class FiktionConfigBoundOverrideTest {
    @Test
    fun `numeric min and max constraints compose with current candidates`() {
        assertRangeEquals(15, Int.MAX_VALUE, selectedIntRange { this using FiktionConfig.Int.min(15) })
        assertRangeEquals(Int.MIN_VALUE, 18, selectedIntRange { this using FiktionConfig.Int.max(18) })
        assertRangeEquals(
            15,
            18,
            selectedIntRange {
                this using FiktionConfig.Int.min(15)
                this using FiktionConfig.Int.max(18)
            },
        )
    }

    @Test
    fun `range resets candidates and min max constraints narrow candidates in declaration order`() {
        assertRangeEquals(
            15,
            20,
            selectedIntRange {
                this using FiktionConfig.Int.range(10..20)
                this using FiktionConfig.Int.min(15)
            },
        )
        assertRangeEquals(
            10,
            18,
            selectedIntRange {
                this using FiktionConfig.Int.range(10..20)
                this using FiktionConfig.Int.max(18)
            },
        )
        assertRangeEquals(
            10,
            20,
            selectedIntRange {
                this using FiktionConfig.Int.min(15)
                this using FiktionConfig.Int.range(10..20)
            },
        )
        assertRangeEquals(
            10,
            20,
            selectedIntRange {
                this using FiktionConfig.Int.max(18)
                this using FiktionConfig.Int.range(10..20)
            },
        )
        assertRangeEquals(
            15,
            18,
            selectedIntRange {
                this using FiktionConfig.Int.range(10..20)
                this using FiktionConfig.Int.min(15)
                this using FiktionConfig.Int.max(18)
            },
        )
        assertRangeEquals(
            10,
            20,
            selectedIntRange {
                this using FiktionConfig.Int.min(15)
                this using FiktionConfig.Int.max(18)
                this using FiktionConfig.Int.range(10..20)
            },
        )
    }

    @Test
    fun `excluding removes multiple candidate ranges and range resets exclusions`() {
        repeat(100) { index ->
            val value =
                fake<Int>(seed = index.toLong()) {
                    this using FiktionConfig.Int.range(1..10)
                    this using FiktionConfig.Int.excluding(listOf(3..5, 8..8))
                }

            assertTrue(value in 1..2 || value in 6..7 || value in 9..10)
        }

        val reset =
            fake<Int> {
                this using FiktionConfig.Int.range(1..10)
                this using FiktionConfig.Int.excluding(listOf(5..5))
                this using FiktionConfig.Int.range(5)
            }

        assertEquals(5, reset)
    }

    @Test
    fun `excluding applies to generated length size bounds step and temporal candidates`() {
        repeat(100) { index ->
            assertTrue(
                fake<String>(seed = index.toLong()) {
                    this using FiktionConfig.String.length(1..10)
                    this using FiktionConfig.String.excludingLengths(listOf(3..5))
                }.length !in 3..5,
            )
            fake<IntRange>(seed = index.toLong()) {
                this using FiktionConfig.IntRange.bounds(1..10)
                this using FiktionConfig.IntRange.excludingBounds(listOf(3..5))
            }.let { range ->
                assertTrue(range.first !in 3..5)
                assertTrue(range.last !in 3..5)
            }
            assertTrue(
                fake<IntProgression>(seed = index.toLong()) {
                    this using FiktionConfig.IntProgression.step(1..10)
                    this using FiktionConfig.IntProgression.excludingSteps(listOf(3..5))
                }.step !in 3..5,
            )
            assertTrue(
                fake<kotlin.time.Duration>(seed = index.toLong()) {
                    this using FiktionConfig.Duration.range(1.seconds..10.seconds)
                    this using FiktionConfig.Duration.excluding(listOf(3.seconds..5.seconds))
                } !in 3.seconds..5.seconds,
            )
            assertTrue(
                fake<Instant>(seed = index.toLong()) {
                    this using FiktionConfig.Instant.epochSeconds(1L..10L)
                    this using FiktionConfig.Instant.excludingEpochSeconds(listOf(3L..5L))
                    this using FiktionConfig.Instant.nanosecond(0)
                }.epochSeconds !in 3L..5L,
            )
            assertTrue(
                fake<Regex>(seed = index.toLong()) {
                    this using FiktionConfig.Regex.length(1..10)
                    this using FiktionConfig.Regex.excludingLengths(listOf(3..5))
                }.pattern.length !in 3..5,
            )
        }
    }

    @Test
    fun `all excluding config keys are available`() {
        val configs =
            listOf<FiktionConfigSettingGroup<*>>(
                FiktionConfig.Int.excluding(listOf(1..1)),
                FiktionConfig.Long.excluding(listOf(1L..1L)),
                FiktionConfig.Float.excluding(listOf(1f..2f)),
                FiktionConfig.Double.excluding(listOf(1.0..2.0)),
                FiktionConfig.Byte.excluding(listOf(1..1)),
                FiktionConfig.Short.excluding(listOf(1..1)),
                FiktionConfig.UInt.excluding(listOf(1u..1u)),
                FiktionConfig.ULong.excluding(listOf(1uL..1uL)),
                FiktionConfig.UByte.excluding(listOf(1u..1u)),
                FiktionConfig.UShort.excluding(listOf(1u..1u)),
                FiktionConfig.IntRange.excludingBounds(listOf(1..1)),
                FiktionConfig.LongRange.excludingBounds(listOf(1L..1L)),
                FiktionConfig.UIntRange.excludingBounds(listOf(1u..1u)),
                FiktionConfig.ULongRange.excludingBounds(listOf(1uL..1uL)),
                FiktionConfig.IntProgression.excludingBounds(listOf(1..1)),
                FiktionConfig.IntProgression.excludingSteps(listOf(1..1)),
                FiktionConfig.LongProgression.excludingBounds(listOf(1L..1L)),
                FiktionConfig.LongProgression.excludingSteps(listOf(1L..1L)),
                FiktionConfig.UIntProgression.excludingBounds(listOf(1u..1u)),
                FiktionConfig.UIntProgression.excludingSteps(listOf(1..1)),
                FiktionConfig.ULongProgression.excludingBounds(listOf(1uL..1uL)),
                FiktionConfig.ULongProgression.excludingSteps(listOf(1L..1L)),
                FiktionConfig.Char.excludingSteps(listOf(1..1)),
                FiktionConfig.String.excludingLengths(listOf(1..1)),
                FiktionConfig.Regex.excludingLengths(listOf(1..1)),
                FiktionConfig.Collection.excludingSizes(listOf(1..1)),
                FiktionConfig.Map.excludingSizes(listOf(1..1)),
                FiktionConfig.Array.excludingSizes(listOf(1..1)),
                FiktionConfig.Duration.excluding(listOf(1.seconds..1.seconds)),
                FiktionConfig.Instant.excludingEpochSeconds(listOf(1L..1L)),
                FiktionConfig.Instant.excludingNanoseconds(listOf(1..1)),
            )

        assertEquals(31, configs.size)
    }

    @Test
    fun `representative numeric types support one-sided constraints`() {
        assertRangeEquals(
            5L,
            8L,
            selectedLongRange {
                this using FiktionConfig.Long.range(0L..10L)
                this using FiktionConfig.Long.min(5L)
                this using FiktionConfig.Long.max(8L)
            },
        )
        assertRangeEquals(
            0.25f,
            0.75f,
            selectedFloatRange {
                this using FiktionConfig.Float.min(0.25f)
                this using FiktionConfig.Float.max(0.75f)
            },
        )
        assertRangeEquals(
            2u,
            4u,
            selectedUIntRange {
                this using FiktionConfig.UInt.range(0u..10u)
                this using FiktionConfig.UInt.min(2u)
                this using FiktionConfig.UInt.max(4u)
            },
        )
    }

    @Test
    fun `length size bounds step and instant overrides compose in declaration order`() {
        assertRangeEquals(
            4,
            6,
            selectedStringLength {
                this using FiktionConfig.String.length(1..8)
                this using FiktionConfig.String.minLength(4)
                this using FiktionConfig.String.maxLength(6)
            },
        )
        assertRangeEquals(
            2,
            4,
            selectedCollectionSize {
                this using FiktionConfig.Collection.size(1..5)
                this using FiktionConfig.Collection.minSize(2)
                this using FiktionConfig.Collection.maxSize(4)
            },
        )
        assertRangeEquals(
            10L,
            20L,
            selectedLongRangeBounds {
                this using FiktionConfig.LongRange.minBound(10L)
                this using FiktionConfig.LongRange.maxBound(20L)
            },
        )
        assertRangeEquals(
            2,
            3,
            selectedIntProgressionStep {
                this using FiktionConfig.IntProgression.minStep(2)
                this using FiktionConfig.IntProgression.maxStep(3)
            },
        )
        assertRangeEquals(
            1_000L,
            2_000L,
            selectedInstantEpochSeconds {
                this using FiktionConfig.Instant.epochSeconds(0L..3_000L)
                this using FiktionConfig.Instant.minEpochSeconds(1_000L)
                this using FiktionConfig.Instant.maxEpochSeconds(2_000L)
            },
        )
        assertRangeEquals(
            10,
            20,
            selectedInstantNanosecond {
                this using FiktionConfig.Instant.minNanosecond(10)
                this using FiktionConfig.Instant.maxNanosecond(20)
            },
        )
    }

    @Test
    fun `higher priority property config composes over broader config`() {
        val fiktion =
            Fiktion {
                register(boundOverrideUserMetadata())
                this using FiktionConfig.String.length(4..10)
                this using FiktionConfig.String.maxLength(8)
            }

        val user =
            fiktion.fake<BoundOverrideUser>(seed = 1) {
                BoundOverrideUser::id using FiktionConfig.String.minLength(6)
            }

        assertTrue(user.id.length in 6..8)
        assertTrue(user.nickname.length in 4..8)
    }

    @Test
    fun `container element key and value configs compose with one-sided overrides`() {
        val value =
            fake<List<Map<String, Int>>>(seed = 1) {
                this using FiktionConfig.Collection.size(1)
                element using FiktionConfig.Map.size(2)
                element.key {
                    this using FiktionConfig.String.length(2..8)
                    this using FiktionConfig.String.minLength(4)
                    this using FiktionConfig.String.maxLength(6)
                }
                element.value {
                    this using FiktionConfig.Int.range(10..30)
                    this using FiktionConfig.Int.min(20)
                    this using FiktionConfig.Int.max(25)
                }
            }

        assertEquals(1, value.size)
        assertEquals(2, value.single().size)
        assertTrue(value.single().keys.all { key -> key.length in 4..6 })
        assertTrue(value.single().values.all { entryValue -> entryValue in 20..25 })
    }

    @Test
    fun `duration min and max overrides compose`() {
        assertRangeEquals(
            1.seconds,
            2.seconds,
            selectedDurationRange {
                this using FiktionConfig.Duration.min(1.seconds)
                this using FiktionConfig.Duration.max(2.seconds)
            },
        )
    }

    @Test
    fun `invalid one-sided overrides fail as configuration errors`() {
        assertFailsWith<FiktionConfigurationException> {
            fake<Int> {
                this using FiktionConfig.Int.min(20)
                this using FiktionConfig.Int.max(10)
            }
        }
        assertFailsWith<FiktionConfigurationException> {
            fake<String> {
                this using FiktionConfig.String.minLength(-1)
            }
        }
        assertFailsWith<FiktionConfigurationException> {
            fake<List<Int>> {
                this using FiktionConfig.Collection.maxSize(-1)
            }
        }
        assertFailsWith<FiktionConfigurationException> {
            fake<IntProgression> {
                this using FiktionConfig.IntProgression.minStep(0)
            }
        }
    }
}

private fun <Value : Comparable<Value>> assertRangeEquals(
    start: Value,
    endInclusive: Value,
    actual: ClosedRange<Value>,
) {
    assertEquals(start, actual.start)
    assertEquals(endInclusive, actual.endInclusive)
}

private fun selectedIntRange(configure: FakeSpec<Int>.() -> Unit): ClosedRange<Int> {
    lateinit var selected: ClosedRange<Int>
    val fiktion =
        Fiktion {
            type<Int>() generatesBy {
                selected = selectedRange(FiktionConfig.Int.min, FiktionConfig.Int.max, FiktionConfig.Int.excluding)
                0
            }
        }
    fiktion.fake<Int> { configure() }
    return selected
}

private fun selectedLongRange(configure: FakeSpec<Long>.() -> Unit): ClosedRange<Long> {
    lateinit var selected: ClosedRange<Long>
    val fiktion =
        Fiktion {
            type<Long>() generatesBy {
                selected = selectedRange(FiktionConfig.Long.min, FiktionConfig.Long.max, FiktionConfig.Long.excluding)
                0L
            }
        }
    fiktion.fake<Long> { configure() }
    return selected
}

private fun selectedFloatRange(configure: FakeSpec<Float>.() -> Unit): ClosedRange<Float> {
    lateinit var selected: ClosedRange<Float>
    val fiktion =
        Fiktion {
            type<Float>() generatesBy {
                selected = selectedRange(FiktionConfig.Float.min, FiktionConfig.Float.max, FiktionConfig.Float.excluding)
                0f
            }
        }
    fiktion.fake<Float> { configure() }
    return selected
}

private fun selectedUIntRange(configure: FakeSpec<UInt>.() -> Unit): ClosedRange<UInt> {
    lateinit var selected: ClosedRange<UInt>
    val fiktion =
        Fiktion {
            type<UInt>() generatesBy {
                selected = selectedRange(FiktionConfig.UInt.min, FiktionConfig.UInt.max, FiktionConfig.UInt.excluding)
                0u
            }
        }
    fiktion.fake<UInt> { configure() }
    return selected
}

private fun selectedStringLength(configure: FakeSpec<String>.() -> Unit): ClosedRange<Int> {
    lateinit var selected: ClosedRange<Int>
    val fiktion =
        Fiktion {
            type<String>() generatesBy {
                selected =
                    selectedRange(
                        FiktionConfig.String.minLength,
                        FiktionConfig.String.maxLength,
                        FiktionConfig.String.excludingLengths,
                    )
                ""
            }
        }
    fiktion.fake<String> { configure() }
    return selected
}

private fun selectedCollectionSize(configure: FakeSpec<List<Int>>.() -> Unit): ClosedRange<Int> {
    lateinit var selected: ClosedRange<Int>
    val fiktion =
        Fiktion {
            type<List<Int>>() generatesBy {
                selected =
                    selectedRange(
                        FiktionConfig.Collection.minSize,
                        FiktionConfig.Collection.maxSize,
                        FiktionConfig.Collection.excludingSizes,
                    )
                emptyList()
            }
        }
    fiktion.fake<List<Int>> { configure() }
    return selected
}

private fun selectedLongRangeBounds(configure: FakeSpec<LongRange>.() -> Unit): ClosedRange<Long> {
    lateinit var selected: ClosedRange<Long>
    val fiktion =
        Fiktion {
            type<LongRange>() generatesBy {
                selected =
                    selectedRange(
                        FiktionConfig.LongRange.minBound,
                        FiktionConfig.LongRange.maxBound,
                        FiktionConfig.LongRange.excludingBounds,
                    )
                0L..0L
            }
        }
    fiktion.fake<LongRange> { configure() }
    return selected
}

private fun selectedIntProgressionStep(configure: FakeSpec<IntProgression>.() -> Unit): ClosedRange<Int> {
    lateinit var selected: ClosedRange<Int>
    val fiktion =
        Fiktion {
            type<IntProgression>() generatesBy {
                selected =
                    selectedRange(
                        FiktionConfig.IntProgression.minStep,
                        FiktionConfig.IntProgression.maxStep,
                        FiktionConfig.IntProgression.excludingSteps,
                    )
                0..0
            }
        }
    fiktion.fake<IntProgression> { configure() }
    return selected
}

private fun selectedInstantEpochSeconds(configure: FakeSpec<Instant>.() -> Unit): ClosedRange<Long> {
    lateinit var selected: ClosedRange<Long>
    val fiktion =
        Fiktion {
            type<Instant>() generatesBy {
                selected =
                    selectedRange(
                        FiktionConfig.Instant.minEpochSeconds,
                        FiktionConfig.Instant.maxEpochSeconds,
                        FiktionConfig.Instant.excludingEpochSeconds,
                    )
                Instant.fromEpochSeconds(0)
            }
        }
    fiktion.fake<Instant> { configure() }
    return selected
}

private fun selectedInstantNanosecond(configure: FakeSpec<Instant>.() -> Unit): ClosedRange<Int> {
    lateinit var selected: ClosedRange<Int>
    val fiktion =
        Fiktion {
            type<Instant>() generatesBy {
                selected =
                    selectedRange(
                        FiktionConfig.Instant.minNanosecond,
                        FiktionConfig.Instant.maxNanosecond,
                        FiktionConfig.Instant.excludingNanoseconds,
                    )
                Instant.fromEpochSeconds(0)
            }
        }
    fiktion.fake<Instant> { configure() }
    return selected
}

private fun selectedDurationRange(configure: FakeSpec<kotlin.time.Duration>.() -> Unit): ClosedRange<kotlin.time.Duration> {
    lateinit var selected: ClosedRange<kotlin.time.Duration>
    val fiktion =
        Fiktion {
            type<kotlin.time.Duration>() generatesBy {
                selected = selectedRange(FiktionConfig.Duration.min, FiktionConfig.Duration.max, FiktionConfig.Duration.excluding)
                kotlin.time.Duration.ZERO
            }
        }
    fiktion.fake<kotlin.time.Duration> { configure() }
    return selected
}

private fun <Scope, Value> FakeContext.selectedRange(
    min: FiktionConfig<Scope, Value>,
    max: FiktionConfig<Scope, Value>,
    excluding: FiktionConfig<Scope, List<ClosedRange<Value>>>,
): ClosedRange<Value> where Value : Any, Value : Comparable<Value> =
    (this as DefaultFakeContext).configState.run {
        val start = selectConfig(key = min, request = request)
        val endInclusive = selectConfig(key = max, request = request)
        selectConfig(key = excluding, request = request)
        start..endInclusive
    }

private data class BoundOverrideUser(
    val id: String,
    val nickname: String,
)

@Suppress("UNCHECKED_CAST")
private fun boundOverrideUserMetadata(): FiktionObjectMetadata<BoundOverrideUser> =
    FiktionObjectMetadata(
        type = typeOf<BoundOverrideUser>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "id", type = typeOf<String>()),
                FiktionObjectProperty(name = "nickname", type = typeOf<String>()),
            ),
    ) { values ->
        BoundOverrideUser(
            id = values[0].valueOrDefault(defaultValue = null) as String,
            nickname = values[1].valueOrDefault(defaultValue = null) as String,
        )
    }
