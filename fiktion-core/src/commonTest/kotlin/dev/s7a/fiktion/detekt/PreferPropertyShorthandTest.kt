package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.FakeSpec
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.invoke
import dev.s7a.fiktion.using
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferPropertyShorthandTest {
    @Test
    fun `property shorthand is equivalent to property call target`() {
        val propertyCall = FakeSpec<User>()
        val shorthand = FakeSpec<User>()

        with(propertyCall) {
            property(User::id) using FiktionConfig.String.length(8)
            property(User::id) generates "user-id"
        }
        with(shorthand) {
            User::id using FiktionConfig.String.length(8)
            User::id generates "user-id"
        }

        assertEquals(propertyCall.configs, shorthand.configs)
        assertEquals(propertyCall.rules.map { spec -> spec.key }, shorthand.rules.map { spec -> spec.key })
    }
}
