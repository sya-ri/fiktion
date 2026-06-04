package dev.s7a.fiktion

import dev.s7a.fiktion.ExperimentalFiktionApi
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class FiktionTest {
    @Test
    fun `invoke creates an isolated instance with configured rules`() {
        val fiktion =
            Fiktion {
                type<User>() generates User(id = "user-1")
            }

        assertEquals(User(id = "user-1"), fiktion.fake<User>())
    }

    @Test
    fun `isolated instances use global rules as their base configuration`() {
        val snapshot =
            Fiktion.configure {
                type<User>() generates User(id = "global-user")
            }

        try {
            assertEquals(User(id = "global-user"), Fiktion().fake<User>())
        } finally {
            snapshot.restore(force = true)
        }
    }

    @Test
    fun `isolated instance rules override global rules`() {
        val snapshot =
            Fiktion.configure {
                type<User>() generates User(id = "global-user")
            }

        try {
            val fiktion =
                Fiktion {
                    type<User>() generates User(id = "instance-user")
                }

            assertEquals(User(id = "instance-user"), fiktion.fake<User>())
        } finally {
            snapshot.restore(force = true)
        }
    }

    @Test
    fun `isolated instance metadata does not leak to other isolated instances`() {
        val registered =
            Fiktion {
                register(userMetadata())
                type<String>() generates "registered-id"
            }
        val empty = Fiktion()

        assertEquals(User(id = "registered-id"), registered.fake<User>())
        val error =
            assertFailsWith<CannotGenerateException> {
                empty.fake<User>(seed = 1)
            }
        val message = error.message.orEmpty()
        assertTrue(message.contains("fake<T>() or Fiktion.fake<T>() is called directly"))
        assertFalse(message.contains("local"))
    }

    @Test
    fun `configure returns a snapshot that restores the previous global configuration`() {
        assertFailsWith<CannotGenerateException> {
            fake<User>(seed = 1)
        }

        val snapshot =
            Fiktion.configure {
                type<User>() generates User(id = "global-user")
            }

        try {
            assertEquals(User(id = "global-user"), fake<User>())
        } finally {
            assertTrue(snapshot.restore())
        }

        assertFailsWith<CannotGenerateException> {
            fake<User>(seed = 1)
        }
    }

    @Test
    fun `configure registers object metadata for top-level fake calls`() {
        val snapshot =
            Fiktion.configure {
                register(userMetadata())
                type<String>() generates "global-id"
            }

        try {
            assertEquals(User(id = "global-id"), fake<User>())
        } finally {
            assertTrue(snapshot.restore(force = true))
        }

        assertFailsWith<CannotGenerateException> {
            fake<User>(seed = 1)
        }
    }

    @Test
    fun `registerGeneratedMetadata registers metadata for top-level fake calls`() {
        Fiktion.registerGeneratedMetadata(constantMetadata { GeneratedUser(id = "generated-user") })

        assertEquals(GeneratedUser(id = "generated-user"), fake<GeneratedUser>())
    }

    @Test
    fun `snapshot restore does not remove generated metadata`() {
        Fiktion.registerGeneratedMetadata(constantMetadata { RestoredGeneratedUser(id = "generated-user") })

        val snapshot =
            Fiktion.configure {
                register(constantMetadata { RestoredGeneratedUser(id = "configured-user") })
            }

        try {
            assertEquals(RestoredGeneratedUser(id = "configured-user"), fake<RestoredGeneratedUser>())
        } finally {
            assertTrue(snapshot.restore(force = true))
        }

        assertEquals(RestoredGeneratedUser(id = "generated-user"), fake<RestoredGeneratedUser>())
    }

    @Test
    fun `registerAutomaticAddon applies to isolated instances created before registration`() {
        val fiktion = Fiktion()

        Fiktion.registerAutomaticAddon(AutomaticAddonTestAddon)

        assertEquals(AutomaticAddonValue(id = "automatic-addon"), fiktion.fake<AutomaticAddonValue>())
    }

    @Test
    fun `generates auto uses rules from automatic add-ons`() {
        val fiktion =
            Fiktion {
                type<AutomaticAddonAutoValue>() generates auto
            }

        Fiktion.registerAutomaticAddon(AutomaticAddonAutoTestAddon)

        assertEquals(AutomaticAddonAutoValue(id = "automatic-addon-auto"), fiktion.fake<AutomaticAddonAutoValue>())
    }

    @Test
    fun `snapshot restore returns false and does not overwrite a newer global configuration`() {
        val first =
            Fiktion.configure {
                type<User>() generates User(id = "first")
            }
        val second =
            Fiktion.configure {
                type<User>() generates User(id = "second")
            }

        try {
            assertFalse(first.restore())

            assertEquals(User(id = "second"), fake<User>())
        } finally {
            second.restore(force = true)
            first.restore(force = true)
        }
    }

    @Test
    fun `snapshot restore returns false after an equivalent newer global configuration`() {
        val first =
            Fiktion.configure {
                type<User>() generates User(id = "same")
            }
        val second =
            Fiktion.configure {
                type<User>() generates User(id = "same")
            }

        try {
            assertFalse(first.restore())

            assertEquals(User(id = "same"), fake<User>())
        } finally {
            second.restore(force = true)
            first.restore(force = true)
        }
    }

    @Test
    fun `snapshot restore with force overwrites a newer global configuration`() {
        val first =
            Fiktion.configure {
                type<User>() generates User(id = "first")
            }
        val second =
            Fiktion.configure {
                type<User>() generates User(id = "second")
            }

        try {
            assertTrue(first.restore(force = true))

            assertFailsWith<CannotGenerateException> {
                fake<User>(seed = 1)
            }
        } finally {
            second.restore(force = true)
            first.restore(force = true)
        }
    }

    /**
     * Returns metadata for constructing [User] from generated arguments.
     */
    private fun userMetadata(): FiktionObjectMetadata<User> =
        FiktionObjectMetadata(
            type = typeOf<User>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                ),
        ) { values ->
            User(id = values[0].valueOrDefault(defaultValue = null) as String)
        }

    /**
     * Returns metadata for constructing a constant object of [T].
     */
    private inline fun <reified T> constantMetadata(noinline construct: () -> T): FiktionObjectMetadata<T> =
        FiktionObjectMetadata(
            type = typeOf<T>(),
            properties = emptyList(),
        ) {
            construct()
        }
}

private data class AutomaticAddonValue(
    val id: String,
)

private object AutomaticAddonTestAddon : FiktionAddon {
    override val id: String = "automatic-addon-test"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<AutomaticAddonValue>() generates AutomaticAddonValue(id = "automatic-addon")
        }
    }
}

private data class AutomaticAddonAutoValue(
    val id: String,
)

private object AutomaticAddonAutoTestAddon : FiktionAddon {
    override val id: String = "automatic-addon-auto-test"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<AutomaticAddonAutoValue>() generates AutomaticAddonAutoValue(id = "automatic-addon-auto")
        }
    }
}
