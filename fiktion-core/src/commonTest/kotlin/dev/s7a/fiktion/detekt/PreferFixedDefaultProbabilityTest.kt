package dev.s7a.fiktion.detekt

import dev.s7a.fiktion.ExperimentalFiktionApi
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.default
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.valueOrDefault
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalFiktionApi::class)
class PreferFixedDefaultProbabilityTest {
    @Test
    fun `zero default probability is equivalent to the fixed generated value`() {
        val fixed =
            Fiktion {
                register(userMetadata())
                type<String>() generates "id"
                DefaultProbabilityUser::profile generates DefaultProbabilityProfile(nickname = "generated")
            }
        val zeroDefaultProbability =
            Fiktion {
                register(userMetadata())
                type<String>() generates "id"
                DefaultProbabilityUser::profile generates DefaultProbabilityProfile(nickname = "generated") orDefaultAt 0.0
            }

        val seeds = listOf(0L, 1L, 2L, 100L)

        assertEquals(
            seeds.map { seed -> fixed.fake<DefaultProbabilityUser>(seed = seed) },
            seeds.map { seed -> zeroDefaultProbability.fake<DefaultProbabilityUser>(seed = seed) },
        )
    }

    @Test
    fun `full default probability is equivalent to a fixed default value`() {
        val fixedDefault =
            Fiktion {
                register(userMetadata())
                type<String>() generates "id"
                DefaultProbabilityUser::profile generates default
            }
        val fullDefaultProbability =
            Fiktion {
                register(userMetadata())
                type<String>() generates "id"
                DefaultProbabilityUser::profile generates DefaultProbabilityProfile(nickname = "generated") orDefaultAt 1.0
            }

        val seeds = listOf(0L, 1L, 2L, 100L)

        assertEquals(
            seeds.map { seed -> fixedDefault.fake<DefaultProbabilityUser>(seed = seed) },
            seeds.map { seed -> fullDefaultProbability.fake<DefaultProbabilityUser>(seed = seed) },
        )
    }
}

private data class DefaultProbabilityUser(
    val id: String,
    val profile: DefaultProbabilityProfile = DefaultProbabilityProfile(nickname = "default"),
)

private data class DefaultProbabilityProfile(
    val nickname: String,
)

private fun userMetadata() =
    dev.s7a.fiktion.FiktionObjectMetadata(
        type = kotlin.reflect.typeOf<DefaultProbabilityUser>(),
        properties =
            listOf(
                dev.s7a.fiktion.FiktionObjectProperty(name = "id", type = kotlin.reflect.typeOf<String>()),
                dev.s7a.fiktion.FiktionObjectProperty(
                    name = "profile",
                    type = kotlin.reflect.typeOf<DefaultProbabilityProfile>(),
                    hasDefault = true,
                ),
            ),
    ) { values ->
        DefaultProbabilityUser(
            id = values[0].valueOrDefault(defaultValue = null) as String,
            profile =
                values[1].valueOrDefault(
                    defaultValue = DefaultProbabilityProfile(nickname = "default"),
                ) as DefaultProbabilityProfile,
        )
    }
