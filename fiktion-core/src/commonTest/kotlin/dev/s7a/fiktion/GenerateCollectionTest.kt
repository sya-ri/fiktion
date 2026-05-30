package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateCollectionTest {
    @Test
    fun `fake generates lists with configured element rules`() {
        val fiktion =
            Fiktion {
                type<List<String>> {
                    this using FiktionConfig.Collection.size(3)
                    element generatesBy { "item-$seed" }
                }
            }

        val value = fiktion.fake<List<String>>(seed = 123)

        assertEquals(3, value.size)
        assertEquals(value.distinct(), value)
    }

    @Test
    fun `fake generates mutable lists with configured element rules`() {
        val fiktion =
            Fiktion {
                type<MutableList<String>> {
                    this using FiktionConfig.Collection.size(2)
                    element generatesBy { "item-$seed" }
                }
            }

        val value = fiktion.fake<MutableList<String>>(seed = 123)

        assertEquals(2, value.size)
        value += "extra"
        assertEquals(3, value.size)
    }

    @Test
    fun `fake generates sets with configured element rules`() {
        val fiktion =
            Fiktion {
                type<Set<String>> {
                    this using FiktionConfig.Collection.size(3)
                    element generatesBy { "item-$seed" }
                }
            }

        val value = fiktion.fake<Set<String>>(seed = 123)

        assertEquals(3, value.size)
        assertEquals(value.toSet(), value)
    }

    @Test
    fun `fake generates mutable sets with configured element rules`() {
        val fiktion =
            Fiktion {
                type<MutableSet<String>> {
                    this using FiktionConfig.Collection.size(2)
                    element generatesBy { "item-$seed" }
                }
            }

        val value = fiktion.fake<MutableSet<String>>(seed = 123)

        assertEquals(2, value.size)
        value += "extra"
        assertEquals(3, value.size)
    }

    @Test
    fun `fake generates collections with a configured size range`() {
        val fiktion =
            Fiktion {
                type<Collection<String>> {
                    this using FiktionConfig.Collection.size(2..4)
                    element generatesBy { "item-$seed" }
                }
            }

        val value = fiktion.fake<Collection<String>>(seed = 123)

        assertTrue(value.size in 2..4, "Expected a collection size in 2..4, but got ${value.size}.")
    }

    @Test
    fun `fake generates configured concrete collection types`() {
        val fiktion =
            Fiktion {
                configureCollection<CustomCollection<*>> { elements ->
                    CustomCollection(elements)
                }
                type<CustomCollection<String>> {
                    this generates auto
                    this using FiktionConfig.Collection.size(2)
                    element generatesBy { "item-$seed" }
                }
            }

        val value = fiktion.fake<CustomCollection<String>>(seed = 123)

        assertEquals(2, value.size)
        assertTrue(value.all { item -> item.startsWith("item-") })
    }

    @Test
    fun `automatic collection generation uses configured element type rules`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy { "item-$seed" }
                type<List<String>>() using FiktionConfig.Collection.size(3)
            }

        val value = fiktion.fake<List<String>>(seed = 123)

        assertEquals(3, value.size)
        assertEquals(value.distinct(), value)
    }

    @Test
    fun `property targets configure object collection properties`() {
        val fiktion =
            Fiktion {
                register(teamMetadata())
                property(Team::names) {
                    this using FiktionConfig.Collection.size(2)
                    element generatesBy { "member-$seed" }
                }
            }

        val team = fiktion.fake<Team>(seed = 123)

        assertEquals(2, team.names.size)
        assertEquals(team.names.distinct(), team.names)
    }

    @Test
    fun `nested property targets configure object collection properties`() {
        val fiktion =
            Fiktion {
                register(departmentMetadata())
                register(teamMetadata())
                property(Department::team / Team::names) {
                    this using FiktionConfig.Collection.size(2)
                    element generatesBy { "member-$seed" }
                }
            }

        val department = fiktion.fake<Department>(seed = 123)

        assertEquals(2, department.team.names.size)
        assertEquals(department.team.names.distinct(), department.team.names)
    }

    @Test
    fun `per-call collection property targets configure object collection properties`() {
        val fiktion =
            Fiktion {
                register(teamMetadata())
            }

        val team =
            fiktion.fake<Team>(seed = 123) {
                property(Team::names) {
                    this using FiktionConfig.Collection.size(2)
                    element generatesBy { "member-$seed" }
                }
            }

        assertEquals(2, team.names.size)
        assertEquals(team.names.distinct(), team.names)
    }

    /**
     * Returns metadata for constructing [Team].
     */
    private fun teamMetadata(): FiktionObjectMetadata<Team> =
        FiktionObjectMetadata(
            type = typeOf<Team>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "names", type = typeOf<List<String>>()),
                ),
        ) { values ->
            @Suppress("UNCHECKED_CAST")
            val names = values[0].valueOrDefault(defaultValue = null) as List<String>

            Team(names = names)
        }

    /**
     * Returns metadata for constructing [Department].
     */
    private fun departmentMetadata(): FiktionObjectMetadata<Department> =
        FiktionObjectMetadata(
            type = typeOf<Department>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "team", type = typeOf<Team>()),
                ),
        ) { values ->
            Department(team = values[0].valueOrDefault(defaultValue = null) as Team)
        }
}

private class CustomCollection<T>(
    private val values: List<T>,
) : Collection<T> by values
