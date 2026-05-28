package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateCollectionTest {
    @Test
    fun `fake generates lists with a fixed size`() {
        val fiktion =
            Fiktion {
                type<List<String>>() generatesEach { "item-$seed" } withSize 3
            }

        val value = fiktion.fake<List<String>>(seed = 123)

        assertEquals(3, value.size)
        assertEquals(value.distinct(), value)
    }

    @Test
    fun `fake generates mutable lists with a fixed size`() {
        val fiktion =
            Fiktion {
                type<MutableList<String>>() generatesEach { "item-$seed" } withSize 2
            }

        val value = fiktion.fake<MutableList<String>>(seed = 123)

        assertEquals(2, value.size)
        value += "extra"
        assertEquals(3, value.size)
    }

    @Test
    fun `fake generates sets with a fixed size`() {
        val fiktion =
            Fiktion {
                type<Set<String>>() generatesEach { "item-$seed" } withSize 3
            }

        val value = fiktion.fake<Set<String>>(seed = 123)

        assertEquals(3, value.size)
        assertEquals(value.toSet(), value)
    }

    @Test
    fun `fake generates mutable sets with a fixed size`() {
        val fiktion =
            Fiktion {
                type<MutableSet<String>>() generatesEach { "item-$seed" } withSize 2
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
                type<Collection<String>>() generatesEach { "item-$seed" } withSize 2..4
            }

        val value = fiktion.fake<Collection<String>>(seed = 123)

        assertTrue(value.size in 2..4, "Expected a collection size in 2..4, but got ${value.size}.")
    }

    @Test
    fun `autoGenerates returns a collection spec for collection targets`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy { "item-$seed" }
                type<List<String>>().autoGenerates() withSize 3
            }

        val value = fiktion.fake<List<String>>(seed = 123)

        assertEquals(3, value.size)
        assertEquals(value.distinct(), value)
    }

    @Test
    fun `autoGenerates returns a collection spec for collection property targets`() {
        val fiktion =
            Fiktion {
                register(teamMetadata())
                type<String>() generatesBy { "member-$seed" }
            }

        val team =
            fiktion.fake<Team>(seed = 123) {
                Team::names.autoGenerates() withSize 2
            }

        assertEquals(2, team.names.size)
        assertEquals(team.names.distinct(), team.names)
    }

    @Test
    fun `autoGenerates returns a collection spec with a configurable size range`() {
        val fiktion =
            Fiktion {
                type<String>() generatesBy { "item-$seed" }
                type<List<String>>().autoGenerates() withSize 2..4
            }

        val value = fiktion.fake<List<String>>(seed = 123)

        assertTrue(value.size in 2..4, "Expected a collection size in 2..4, but got ${value.size}.")
    }

    @Test
    fun `autoGenerates returns a collection spec for nested collection paths`() {
        val fiktion =
            Fiktion {
                register(departmentMetadata())
                register(teamMetadata())
                type<String>() generatesBy { "member-$seed" }
                property(Department::team / Team::names).autoGenerates() withSize 2
            }

        val department = fiktion.fake<Department>(seed = 123)

        assertEquals(2, department.team.names.size)
        assertEquals(department.team.names.distinct(), department.team.names)
    }

    @Test
    fun `per-call collection property rules generate object collection properties`() {
        val fiktion =
            Fiktion {
                register(teamMetadata())
            }

        val team =
            fiktion.fake<Team>(seed = 123) {
                Team::names generatesEach { "member-$seed" } withSize 2
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
