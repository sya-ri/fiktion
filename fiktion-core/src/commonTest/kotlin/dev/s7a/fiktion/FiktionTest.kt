package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.CannotGenerateException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
}
