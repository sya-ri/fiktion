package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FiktionBuilderTest {
    @Test
    fun `rules with the same key replace earlier rules in the same layer`() {
        val fiktion =
            Fiktion {
                type<User>() generates User(id = "first")
                type<User>() generates User(id = "second")
            }

        assertEquals(User(id = "second"), fiktion.fake<User>())
    }

    @Test
    fun `explicit rules override add-on rules regardless of install order`() {
        val fiktion =
            Fiktion {
                type<User>() generates User(id = "explicit-user")
                install(UserAddon)
            }

        assertEquals(User(id = "explicit-user"), fiktion.fake<User>())
    }

    @Test
    fun `installing an add-on with the same id replaces the previous add-on`() {
        val replacementAddon =
            object : FiktionAddon {
                override val id: String = UserAddon.id

                override fun install(builder: FiktionAddonBuilder) {
                    with(builder) {
                        type<User>() generates User(id = "replacement-user")
                    }
                }
            }

        val fiktion =
            Fiktion {
                install(UserAddon)
                install(replacementAddon)
            }

        assertEquals(User(id = "replacement-user"), fiktion.fake<User>())
    }

    @Test
    fun `mutating a returned generation spec does not change an already built instance`() {
        lateinit var spec: GenerationSpec<User>
        val fiktion =
            Fiktion {
                spec =
                    type<User>() generatesBy {
                        User(id = "user-$seed")
                    }
            }

        spec withSeed 456

        assertEquals(User(id = "user-123"), fiktion.fake<User>(seed = 123))
    }

    @Test
    fun `regex rules with the same pattern and options replace earlier regex rules`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            name<String>(".*Name".toRegex()) generates "first"
            name<String>(".*Name".toRegex()) generates "second"
        }

        assertEquals(1, builder.build().rules.size)
    }

    @Test
    fun `name rules infer value type from generated values`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            name("id") generates "user-1"
        }

        assertEquals(
            RuleKey.Name("id", typeOf<String>()),
            builder
                .build()
                .rules
                .single()
                .key,
        )
    }

    @Test
    fun `name rules can declare the value type explicitly`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            name<String>("id") generates "user-1"
        }

        assertEquals(
            RuleKey.Name("id", typeOf<String>()),
            builder
                .build()
                .rules
                .single()
                .key,
        )
    }

    @Test
    fun `property rules can target owner and property name`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            property<User, String>("id") generates "user-1"
        }

        assertEquals(
            RuleKey.Property(typeOf<User>(), "id", typeOf<String>()),
            builder
                .build()
                .rules
                .single()
                .key,
        )
    }

    @Test
    fun `property rules can be registered with nested property syntax`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            property(User::profile / Profile::nickname) generates "nickname"
        }

        val key =
            assertIs<RuleKey.Path>(
                builder
                    .build()
                    .rules
                    .single()
                    .key,
            )
        assertEquals(listOf("profile", "nickname"), key.segments.map { segment -> segment.name })
    }

    @Test
    fun `range rules generate primitive values inside the configured ranges`() {
        val fiktion =
            Fiktion {
                type<Byte>() generatesIn 10..20
                type<Short>() generatesIn 100..200
                type<Int>() generatesIn 10..20
                type<Long>() generatesIn 100L..200L
                type<Float>() generatesIn 1.5f..2.5f
                type<Double>() generatesIn 1.5..2.5
                type<Char>() generatesIn 'a'..'f'
                type<UByte>() generatesIn 10u..20u
                type<UShort>() generatesIn 100u..200u
                type<UInt>() generatesIn 1000u..2000u
                type<ULong>() generatesIn 1000uL..2000uL
            }

        assertTrue(fiktion.fake<Byte>(seed = 1) in 10..20)
        assertTrue(fiktion.fake<Short>(seed = 1) in 100..200)
        assertTrue(fiktion.fake<Int>(seed = 1) in 10..20)
        assertTrue(fiktion.fake<Long>(seed = 1) in 100L..200L)
        assertTrue(fiktion.fake<Float>(seed = 1) in 1.5f..2.5f)
        assertTrue(fiktion.fake<Double>(seed = 1) in 1.5..2.5)
        assertTrue(fiktion.fake<Char>(seed = 1) in 'a'..'f')
        assertTrue(fiktion.fake<UByte>(seed = 1) in 10u..20u)
        assertTrue(fiktion.fake<UShort>(seed = 1) in 100u..200u)
        assertTrue(fiktion.fake<UInt>(seed = 1) in 1000u..2000u)
        assertTrue(fiktion.fake<ULong>(seed = 1) in 1000uL..2000uL)
    }
}
