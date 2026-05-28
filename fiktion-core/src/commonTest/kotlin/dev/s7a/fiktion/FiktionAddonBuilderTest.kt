package dev.s7a.fiktion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FiktionAddonBuilderTest {
    @Test
    fun `add-on rules are not active until the add-on is installed`() {
        assertFailsWith<CannotGenerateException> {
            fake<AddonToken>(seed = 123)
        }
    }

    @Test
    fun `installed add-on contributes type rules`() {
        val fiktion =
            Fiktion {
                install(ExampleAddon)
            }

        assertEquals(AddonToken("token-123"), fiktion.fake<AddonToken>(seed = 123))
        assertEquals(AddonLabel("label-456"), fiktion.fake<AddonLabel>(seed = 456))
    }

    @Test
    fun `instance rules override installed add-on rules`() {
        val fiktion =
            Fiktion {
                install(ExampleAddon)
                type<AddonToken>() generates AddonToken("instance-token")
            }

        assertEquals(AddonToken("instance-token"), fiktion.fake<AddonToken>(seed = 123))
    }

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

    private data class AddonToken(
        val value: String,
    )

    private data class AddonLabel(
        val value: String,
    )

    private object ExampleAddon : FiktionAddon {
        override val id: String = "test-example-addon"

        override fun install(builder: FiktionAddonBuilder) {
            with(builder) {
                type<AddonToken>() generatesBy {
                    AddonToken("token-$seed")
                }
                type<AddonLabel>() generatesBy {
                    AddonLabel("label-$seed")
                }
            }
        }
    }
}
