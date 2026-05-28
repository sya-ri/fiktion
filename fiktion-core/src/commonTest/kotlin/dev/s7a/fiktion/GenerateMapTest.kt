package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateMapTest {
    @Test
    fun `fake generates maps with entry rules`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize 3
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `fake generates mutable maps with entry rules`() {
        val fiktion =
            Fiktion {
                type<MutableMap<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize 2
            }

        val value = fiktion.fake<MutableMap<String, Int>>(seed = 123)

        assertEquals(2, value.size)
        value["extra"] = 1
        assertEquals(3, value.size)
    }

    @Test
    fun `fake generates maps with key and value rules`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesKeys { "key-$seed" } andValues { seed.toInt() } withSize 3
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `fake generates maps with value and key rules`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesValues { seed.toInt() } andKeys { "key-$seed" } withSize 3
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `fake generates maps with split key and value rules`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesKeys { "key-$seed" } withSize 3
                type<Map<String, Int>>() generatesValues { seed.toInt() }
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `fake generates maps with automatic key and value rules`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy { "key-$seed" }
                type<Int>() generates 7
                type<Map<String, Int>>() generates auto withSize 3
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
        assertEquals(setOf(7), value.values.toSet())
    }

    @Test
    fun `fake generates maps with automatic values when only keys are configured`() {
        val fiktion =
            Fiktion {
                type<Int>() generates 7
                type<Map<String, Int>>() generatesKeys { "key-$seed" } withSize 3
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
        assertEquals(setOf(7), value.values.toSet())
    }

    @Test
    fun `fake generates maps with automatic keys when only values are configured`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy { "key-$seed" }
                type<Map<String, Int>>() generatesValues { seed.toInt() } withSize 3
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `split map rules share a size configured by the second declaration`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesKeys { "key-$seed" }
                type<Map<String, Int>>() generatesValues { seed.toInt() } withSize 2
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(2, value.size)
    }

    @Test
    fun `fake generates empty maps with a zero size`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize 0
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(emptyMap(), value)
    }

    @Test
    fun `fake generates maps with a configured size range`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize 2..4
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertTrue(value.size in 2..4, "Expected a map size in 2..4, but got ${value.size}.")
    }

    @Test
    fun `per-call map property rules automatically generate object map properties`() {
        val fiktion =
            Fiktion {
                register(projectMetadata())
                type<String>() generatesBy { "part-$seed" }
            }

        val project =
            fiktion.fake<Project>(seed = 123) {
                Project::labels generates auto withSize 2
            }

        assertEquals(2, project.labels.size)
        assertTrue(project.labels.keys.all { key -> key.startsWith("part-") })
        assertTrue(project.labels.values.all { value -> value.startsWith("part-") })
    }

    @Test
    fun `global map property rules automatically generate object map properties`() {
        val fiktion =
            Fiktion {
                register(projectMetadata())
                type<String>() generatesBy { "part-$seed" }
                Project::labels generates auto withSize 2
            }

        val project = fiktion.fake<Project>(seed = 123)

        assertEquals(2, project.labels.size)
        assertTrue(project.labels.keys.all { key -> key.startsWith("part-") })
        assertTrue(project.labels.values.all { value -> value.startsWith("part-") })
    }

    @Test
    fun `per-call map property rules generate object map properties`() {
        val fiktion =
            Fiktion {
                register(projectMetadata())
            }

        val project =
            fiktion.fake<Project>(seed = 123) {
                Project::labels generatesEach { "label-$seed" to "value-$seed" } withSize 2
            }

        assertEquals(2, project.labels.size)
        assertTrue(project.labels.keys.all { key -> key.startsWith("label-") })
    }

    @Test
    fun `duplicate map key rules fail immediately`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesKeys { "first-$seed" }
                    type<Map<String, Int>>() generatesKeys { "second-$seed" }
                }
            }

        assertTrue(error.message.orEmpty().contains("Map keys are already configured"))
    }

    @Test
    fun `duplicate map value rules fail immediately`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesValues { seed.toInt() }
                    type<Map<String, Int>>() generatesValues { seed.toInt() + 1 }
                }
            }

        assertTrue(error.message.orEmpty().contains("Map values are already configured"))
    }

    @Test
    fun `duplicate map entry rules fail immediately`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesEach { "first-$seed" to seed.toInt() }
                    type<Map<String, Int>>() generatesEach { "second-$seed" to seed.toInt() }
                }
            }

        assertTrue(error.message.orEmpty().contains("Map entries are already configured"))
    }

    @Test
    fun `map entry rules cannot be combined with key rules`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesKeys { "key-$seed" }
                    type<Map<String, Int>>() generatesEach { "entry-$seed" to seed.toInt() }
                }
            }

        assertTrue(error.message.orEmpty().contains("Map keys are already configured"))
    }

    @Test
    fun `map key rules cannot be combined with entry rules`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesEach { "entry-$seed" to seed.toInt() }
                    type<Map<String, Int>>() generatesKeys { "key-$seed" }
                }
            }

        assertTrue(error.message.orEmpty().contains("Map entries are already configured"))
    }

    @Test
    fun `map sizes cannot be negative`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize -1
                }
            }

        assertTrue(error.message.orEmpty().contains("Map size must be 0 or greater"))
    }

    @Test
    fun `map size ranges cannot be empty`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize IntRange.EMPTY
                }
            }

        assertTrue(error.message.orEmpty().contains("Map size range must not be empty"))
    }

    @Test
    fun `map size ranges cannot start below zero`() {
        val error =
            assertFailsWith<IllegalArgumentException> {
                Fiktion {
                    type<Map<String, Int>>() generatesEach { "key-$seed" to seed.toInt() } withSize -1..1
                }
            }

        assertTrue(error.message.orEmpty().contains("Map size range must start at 0 or greater"))
    }

    /**
     * Returns metadata for constructing [Project].
     */
    private fun projectMetadata(): FiktionObjectMetadata<Project> =
        FiktionObjectMetadata(
            type = typeOf<Project>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "labels", type = typeOf<Map<String, String>>()),
                ),
        ) { values ->
            @Suppress("UNCHECKED_CAST")
            val labels = values[0].valueOrDefault(defaultValue = null) as Map<String, String>

            Project(labels = labels)
        }
}
