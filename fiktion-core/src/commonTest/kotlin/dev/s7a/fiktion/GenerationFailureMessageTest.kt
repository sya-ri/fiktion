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

    @Test
    fun `missing generation message reports root request exactly`() {
        val message =
            missingGenerationMessage(
                request = GenerationRequest(type = typeOf<TextMessage>()),
                config = FiktionConfigState(),
            )

        assertEquals(
            """
            Cannot generate ${typeOf<TextMessage>()}.

            No generation rule or generated metadata was found for ${typeOf<TextMessage>()}.

            Generation request:
            - type: ${typeOf<TextMessage>()}

            Current Fiktion configuration:
            - installed add-ons: none
            - registered metadata entries: 0
            - explicit generation rules: 0
            - explicit generator configs: 0

            Fiktion can automatically generate constructor metadata for supported Kotlin classes when the compiler plugin is enabled.
            If this type should be generated automatically, check that:
            - the Fiktion compiler plugin is applied to this source set
            - fake<T>() or Fiktion.fake<T>() is called directly from a compiler-plugin-enabled source set
            - wrapper functions around fake<T>() are not hiding the direct fake<T>() call from the compiler plugin
            - the type has a supported primary constructor
            - the type is not abstract, an interface, fun interface, inner, or annotation class
            - the primary constructor is not private, protected, vararg, or otherwise unsupported

            If this type comes from a library, install or explicitly register an add-on for that library when one exists.

            To generate this request manually, add one of:
            - type<${typeOf<TextMessage>()}>() generatesBy { ... }
            """.trimIndent(),
            message,
        )
    }

    @Test
    fun `missing generation message reports property request exactly`() {
        val message =
            missingGenerationMessage(
                request =
                    GenerationRequest(
                        type = typeOf<Profile>(),
                        owner = typeOf<User>(),
                        propertyName = "profile",
                        pathSegments =
                            listOf(
                                PathRuleSegment(
                                    ownerId = typeOf<User>().nonNullTypeId(),
                                    name = "profile",
                                    valueId = typeOf<Profile>().nonNullTypeId(),
                                ),
                            ),
                    ),
                config =
                    FiktionConfigState(
                        rules =
                            listOf(
                                DefaultGenerationSpec(
                                    key = RuleKey.Type(typeOf<String>()),
                                    matcher = RuleMatcher.Type(typeOf<String>()),
                                    generator = { "id" },
                                ),
                            ),
                        metadata = mapOf(typeOf<User>().nonNullTypeId() to userMetadataForFailureMessageTest()),
                    ),
            )

        assertEquals(
            """
            Cannot generate ${typeOf<Profile>()}.

            No generation rule or generated metadata was found for ${typeOf<Profile>()}.

            Generation request:
            - type: ${typeOf<Profile>()}
            - owner: ${typeOf<User>()}
            - property: profile
            - path: profile

            Current Fiktion configuration:
            - installed add-ons: none
            - registered metadata entries: 1
            - explicit generation rules: 1
            - explicit generator configs: 0

            Fiktion can automatically generate constructor metadata for supported Kotlin classes when the compiler plugin is enabled.
            If this type should be generated automatically, check that:
            - the Fiktion compiler plugin is applied to this source set
            - fake<T>() or Fiktion.fake<T>() is called directly from a compiler-plugin-enabled source set
            - wrapper functions around fake<T>() are not hiding the direct fake<T>() call from the compiler plugin
            - the type has a supported primary constructor
            - the type is not abstract, an interface, fun interface, inner, or annotation class
            - the primary constructor is not private, protected, vararg, or otherwise unsupported

            If this type comes from a library, install or explicitly register an add-on for that library when one exists.

            To generate this request manually, add one of:
            - type<${typeOf<Profile>()}>() generatesBy { ... }
            - property<${typeOf<User>()}, ${typeOf<Profile>()}>("profile") generatesBy { ... }
            - name<${typeOf<Profile>()}>("profile") generatesBy { ... }
            """.trimIndent(),
            message,
        )
    }
}

private fun userMetadataForFailureMessageTest(): FiktionObjectMetadata<User> =
    FiktionObjectMetadata(
        type = typeOf<User>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "profile", type = typeOf<Profile>()),
            ),
    ) { values ->
        User(id = "id", profile = values[0].valueOrDefault(defaultValue = null) as Profile)
    }
