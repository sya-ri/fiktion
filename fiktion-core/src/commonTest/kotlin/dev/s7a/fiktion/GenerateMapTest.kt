package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateMapTest {
    @Test
    fun `fake generates maps with configured key and value rules`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>> {
                    this using FiktionConfig.Map.size(3)
                    key generatesBy { "key-$index" }
                    value generatesBy { index }
                }
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(
            mapOf(
                "key-0" to 0,
                "key-1" to 1,
                "key-2" to 2,
            ),
            value,
        )
    }

    @Test
    fun `fake generates mutable maps with configured key and value rules`() {
        val fiktion =
            Fiktion {
                type<MutableMap<String, Int>> {
                    this using FiktionConfig.Map.size(2)
                    key generatesBy { "key-$index" }
                    value generatesBy { index }
                }
            }

        val value = fiktion.fake<MutableMap<String, Int>>(seed = 123)

        assertEquals(2, value.size)
        value["extra"] = 1
        assertEquals(3, value.size)
    }

    @Test
    fun `fake generates maps with automatic values when only keys are configured`() {
        val fiktion =
            Fiktion {
                type<Int>() generates 7
                type<Map<String, Int>> {
                    this using FiktionConfig.Map.size(3)
                    key generatesBy { "key-$index" }
                }
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
                type<Map<String, Int>> {
                    this using FiktionConfig.Map.size(3)
                    value generatesBy { index }
                }
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `fake generates empty maps with a zero size`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>> {
                    this using FiktionConfig.Map.size(0)
                    key generatesBy { "key-$index" }
                    value generatesBy { index }
                }
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertEquals(emptyMap(), value)
    }

    @Test
    fun `fake generates maps with a configured size range`() {
        val fiktion =
            Fiktion {
                type<Map<String, Int>> {
                    this using FiktionConfig.Map.size(2..4)
                    key generatesBy { "key-$index" }
                    value generatesBy { index }
                }
            }

        val value = fiktion.fake<Map<String, Int>>(seed = 123)

        assertTrue(value.size in 2..4, "Expected a map size in 2..4, but got ${value.size}.")
    }

    @Test
    fun `fake generates configured concrete map types`() {
        val fiktion =
            Fiktion {
                configureMap<CustomMap<*, *>> { entries ->
                    CustomMap(entries.toMap())
                }
                type<CustomMap<String, Int>> {
                    this generates auto
                    this using FiktionConfig.Map.size(2)
                    key generatesBy { "key-$index" }
                    value generatesBy { index }
                }
            }

        val value = fiktion.fake<CustomMap<String, Int>>(seed = 123)

        assertEquals(2, value.size)
        assertTrue(value.keys.all { key -> key.startsWith("key-") })
    }

    @Test
    fun `per-call map property targets automatically generate object map properties`() {
        val fiktion =
            Fiktion {
                register(projectMetadata())
                type<String>() generatesBy { "part-$seed" }
            }

        val project =
            fiktion.fake<Project>(seed = 123) {
                property(Project::labels) {
                    this using FiktionConfig.Map.size(2)
                }
            }

        assertEquals(2, project.labels.size)
        assertTrue(project.labels.keys.all { key -> key.startsWith("part-") })
        assertTrue(project.labels.values.all { value -> value.startsWith("part-") })
    }

    @Test
    fun `global map property targets automatically generate object map properties`() {
        val fiktion =
            Fiktion {
                register(projectMetadata())
                type<String>() generatesBy { "part-$seed" }
                property(Project::labels) {
                    this using FiktionConfig.Map.size(2)
                }
            }

        val project = fiktion.fake<Project>(seed = 123)

        assertEquals(2, project.labels.size)
        assertTrue(project.labels.keys.all { key -> key.startsWith("part-") })
        assertTrue(project.labels.values.all { value -> value.startsWith("part-") })
    }

    @Test
    fun `per-call map property targets configure object map properties`() {
        val fiktion =
            Fiktion {
                register(projectMetadata())
            }

        val project =
            fiktion.fake<Project>(seed = 123) {
                property(Project::labels) {
                    this using FiktionConfig.Map.size(2)
                    key generatesBy { "label-$index" }
                    value generatesBy { "value-$index" }
                }
            }

        assertEquals(
            mapOf(
                "label-0" to "value-0",
                "label-1" to "value-1",
            ),
            project.labels,
        )
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

private class CustomMap<K, V>(
    private val delegate: Map<K, V>,
) : Map<K, V> by delegate
