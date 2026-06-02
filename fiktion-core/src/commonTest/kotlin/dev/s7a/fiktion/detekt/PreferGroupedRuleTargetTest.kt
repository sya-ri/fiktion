package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.FakeSpec
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferGroupedRuleTargetTest {
    @Test
    fun `grouped type target is equivalent to repeated type targets`() {
        val repeated =
            Fiktion {
                type<String>() using FiktionConfig.String.length(4)
                type<String>() generates "text"
            }
        val grouped =
            Fiktion {
                type<String> {
                    this using FiktionConfig.String.length(4)
                    this generates "text"
                }
            }

        assertEquals(repeated.fake<String>(), grouped.fake<String>())
    }

    @Test
    fun `grouped property target is equivalent to repeated property targets`() {
        val repeated = FakeSpec<User>()
        val grouped = FakeSpec<User>()

        with(repeated) {
            property(User::id) using FiktionConfig.String.length(8)
            property(User::id) generates "user-id"
        }
        with(grouped) {
            property(User::id) {
                this using FiktionConfig.String.length(8)
                this generates "user-id"
            }
        }

        assertEquals(repeated.configs, grouped.configs)
        assertEquals(repeated.rules.map { spec -> spec.key }, grouped.rules.map { spec -> spec.key })
    }

    @Test
    fun `grouped name target is equivalent to repeated name targets`() {
        val repeated = FakeSpec<User>()
        val grouped = FakeSpec<User>()

        with(repeated) {
            name<String>("id") using FiktionConfig.String.length(8)
            name<String>("id") generates "user-id"
        }
        with(grouped) {
            name<String>("id").invoke {
                this using FiktionConfig.String.length(8)
                this generates "user-id"
            }
        }

        assertEquals(repeated.configs, grouped.configs)
        assertEquals(repeated.rules.map { spec -> spec.key }, grouped.rules.map { spec -> spec.key })
    }
}

data class User(
    val id: String,
)
