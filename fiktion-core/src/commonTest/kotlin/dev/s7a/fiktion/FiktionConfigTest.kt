@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import dev.s7a.fiktion.generators.FiktionCharset
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FiktionConfigTest {
    @Test
    fun `instance config changes built-in generator defaults`() {
        val fiktion =
            Fiktion {
                this using FiktionConfig.Int.range(-200..200)
            }

        assertTrue(fiktion.fake<Int>(seed = 1) in -200..200)
    }

    @Test
    fun `per-call root config only accepts matching config scope`() {
        val value =
            fake<Int>(seed = 1) {
                this using FiktionConfig.Int.range(10..20)
            }

        assertTrue(value in 10..20)
    }

    @Test
    fun `per-call root config accepts supertype config scope`() {
        val value =
            fake<List<Int>>(seed = 1) {
                this using FiktionConfig.Collection.size(5..5)
            }

        assertEquals(5, value.size)
    }

    @Test
    fun `instance config applies to nested collection elements`() {
        val fiktion =
            Fiktion {
                this using FiktionConfig.Collection.size(5..5)
                this using FiktionConfig.Int.range(10..20)
            }

        val value = fiktion.fake<List<Int>>(seed = 1)

        assertEquals(5, value.size)
        assertTrue(value.all { element -> element in 10..20 })
    }

    @Test
    fun `property config applies to the selected property`() {
        val fiktion =
            Fiktion {
                register(configUserMetadata())
                register(configProfileMetadata())
            }

        val user =
            fiktion.fake<ConfigUser>(seed = 1) {
                ConfigUser::id using FiktionConfig.String.length(4..4)
            }

        assertEquals(4, user.id.length)
    }

    @Test
    fun `nested property config applies to the selected nested property`() {
        val fiktion =
            Fiktion {
                register(configUserMetadata())
                register(configProfileMetadata())
            }

        val user =
            fiktion.fake<ConfigUser>(seed = 1) {
                ConfigUser::profile {
                    ConfigProfile::nickname using FiktionConfig.String.length(6..6)
                }
            }

        assertEquals(6, user.profile.nickname.length)
    }

    @Test
    fun `property config overrides broader config`() {
        val fiktion =
            Fiktion {
                register(configUserMetadata())
                register(configProfileMetadata())
                this using FiktionConfig.String.length(8..8)
            }

        val user =
            fiktion.fake<ConfigUser>(seed = 1) {
                ConfigUser::id using FiktionConfig.String.length(4..4)
            }

        assertEquals(4, user.id.length)
        assertEquals(8, user.profile.nickname.length)
    }

    @Test
    fun `string charset config changes built-in generator defaults`() {
        val value =
            fake<String>(seed = 1) {
                this using FiktionConfig.String.length(8..8)
                this using FiktionConfig.String.charset(FiktionCharset.Numeric)
            }

        assertEquals(8, value.length)
        assertTrue(value.all { char -> char.isDigit() })
    }
}

private data class ConfigUser(
    val id: String,
    val profile: ConfigProfile = ConfigProfile(nickname = ""),
)

private data class ConfigProfile(
    val nickname: String,
)

private fun configUserMetadata(): FiktionObjectMetadata<ConfigUser> =
    FiktionObjectMetadata(
        type = typeOf<ConfigUser>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "id", type = typeOf<String>()),
                FiktionObjectProperty(name = "profile", type = typeOf<ConfigProfile>()),
            ),
    ) { values ->
        ConfigUser(
            id = values[0].valueOrDefault(defaultValue = null) as String,
            profile = values[1].valueOrDefault(defaultValue = null) as ConfigProfile,
        )
    }

private fun configProfileMetadata(): FiktionObjectMetadata<ConfigProfile> =
    FiktionObjectMetadata(
        type = typeOf<ConfigProfile>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "nickname", type = typeOf<String>()),
            ),
    ) { values ->
        ConfigProfile(nickname = values[0].valueOrDefault(defaultValue = null) as String)
    }
