package dev.s7a.fiktion.compatibility

import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import kotlin.test.Test
import kotlin.test.assertEquals

data class CompatibilityUser(
    val id: String,
    val profile: CompatibilityProfile,
)

data class CompatibilityProfile(
    val displayName: String,
)

class FiktionCompatibilityTest {
    @Test
    fun `consumer project can use published fiktion artifacts`() {
        val user = fake<CompatibilityUser>(seed = 123) {
            CompatibilityUser::id generates "user-1"
        }

        assertEquals("user-1", user.id)
        assertEquals(
            user,
            fake<CompatibilityUser>(seed = 123) {
                CompatibilityUser::id generates "user-1"
            },
        )
    }
}
