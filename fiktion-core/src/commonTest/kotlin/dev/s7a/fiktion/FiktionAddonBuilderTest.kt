package dev.s7a.fiktion

import kotlin.test.Test
import kotlin.test.assertEquals

class FiktionAddonBuilderTest {
    @Test
    fun `rule seed configured by an add-on is applied to the installed rule`() {
        val addon =
            object : FiktionAddon {
                override val id: String = "test-seeded-addon"

                override fun install(builder: FiktionAddonBuilder) {
                    with(builder) {
                        type<User>() generatesBy {
                            User(id = "user-$seed")
                        } withSeed 456
                    }
                }
            }
        val fiktion =
            Fiktion {
                withSeed(123)
                install(addon)
            }

        assertEquals(User(id = "user-456"), fiktion.fake<User>())
    }
}
