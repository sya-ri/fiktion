@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class GenerationFailureMessageTest {
    @Test
    fun `empty sealed metadata message is reported exactly`() {
        assertEquals(
            """
            Cannot generate ${typeOf<Message>()}.

            Generated sealed metadata for ${typeOf<Message>()} has no concrete subtypes.

            Check that the sealed hierarchy has concrete object or class leaves in the same compilation scope.
            To override selection manually, add an explicit rule:
            type<${typeOf<Message>()}>() generatesBy { ... }
            """.trimIndent(),
            emptySealedMetadataMessage(typeOf<Message>()),
        )
    }

    @Test
    fun `value underlying generation message is reported exactly`() {
        assertEquals(
            """
            Cannot generate ${typeOf<ComplexUserId>()}.

            Generated value metadata was found for ${typeOf<ComplexUserId>()}, but its underlying value type ${typeOf<ComplexUserIdValue>()} could not be generated.

            Add a rule for the underlying type:
            type<${typeOf<ComplexUserIdValue>()}>() generatesBy { ... }

            Or override the value class directly:
            type<${typeOf<ComplexUserId>()}>() generatesBy { ... }
            """.trimIndent(),
            valueUnderlyingGenerationMessage(
                type = typeOf<ComplexUserId>(),
                underlyingType = typeOf<ComplexUserIdValue>(),
            ),
        )
    }

    @Test
    fun `object argument generation message is reported exactly`() {
        assertEquals(
            """
            Cannot generate ${typeOf<User>()}.

            Failed while generating constructor argument profile: ${typeOf<Profile>()}.

            Add a rule for the nested type or property:
            type<${typeOf<Profile>()}>() generatesBy { ... }
            property<${typeOf<User>()}, ${typeOf<Profile>()}>("profile") generatesBy { ... }
            """.trimIndent(),
            objectArgumentGenerationMessage(
                type = typeOf<User>(),
                property = FiktionObjectProperty(name = "profile", type = typeOf<Profile>()),
            ),
        )
    }

    @Test
    fun `missing default value message is reported exactly`() {
        assertEquals(
            """
            Cannot generate ${typeOf<User>()}.

            Rule requested the constructor default for profile: ${typeOf<Profile>()}, but that constructor argument has no default value.

            Use `generates default` only for constructor arguments with default values, or generate an explicit value:
            property<${typeOf<User>()}, ${typeOf<Profile>()}>("profile") generatesBy { ... }
            """.trimIndent(),
            missingDefaultValueMessage(
                type = typeOf<User>(),
                property = FiktionObjectProperty(name = "profile", type = typeOf<Profile>()),
            ),
        )
    }

    @Test
    fun `default value without constructor argument message is reported exactly`() {
        assertEquals(
            """
            Cannot generate ${typeOf<String>()}.

            Rule requested a constructor default, but no constructor argument default is available for this generation request.

            Use `generates default` only for constructor arguments with default values, or generate an explicit value:
            type<${typeOf<String>()}>() generatesBy { ... }
            """.trimIndent(),
            defaultValueWithoutConstructorArgumentMessage(typeOf<String>()),
        )
    }
}
