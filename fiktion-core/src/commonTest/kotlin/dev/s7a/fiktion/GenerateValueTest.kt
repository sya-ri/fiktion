@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GenerateValueTest {
    @Test
    fun `generateValue passes fake context to the matching rule generator`() {
        val fiktion =
            Fiktion {
                type<User>() generatesBy {
                    User(id = "user-$seed")
                }
            }

        assertEquals(User(id = "user-123"), fiktion.fake<User>(seed = 123))
    }

    @Test
    fun `generateValue uses the rule seed when the matching rule has one`() {
        val fiktion =
            Fiktion {
                type<User>() generatesBy {
                    User(id = "user-$seed")
                } withSeed 456
            }

        assertEquals(User(id = "user-456"), fiktion.fake<User>(seed = 123))
    }

    @Test
    fun `generateValue prefers a more specific rule within the same precedence layer`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            property<User, String>("id") generates "property-id"
            type<String>() generates "type-string"
        }

        val value =
            generateValue(
                request =
                    GenerationRequest(
                        type = typeOf<String>(),
                        owner = typeOf<User>(),
                        propertyName = "id",
                    ),
                config = builder.build(),
                seed = 123,
                depth = 0,
            )

        assertEquals("property-id", value)
    }

    @Test
    fun `generateValue prefers a higher precedence layer before specificity`() {
        val base =
            FiktionConfig(
                rules =
                    listOf(
                        RegisteredRule(
                            key = RuleKey.Property(typeOf<User>(), "id", typeOf<String>()),
                            matcher = RuleMatcher.Property(typeOf<User>(), "id", typeOf<String>()),
                            generator = { "global-property-id" },
                        ),
                    ),
            )
        val overlay =
            FiktionConfig(
                rules =
                    listOf(
                        RegisteredRule(
                            key = RuleKey.Type(typeOf<String>()),
                            matcher = RuleMatcher.Type(typeOf<String>()),
                            generator = { "per-call-string" },
                        ),
                    ),
            )

        val value =
            generateValue(
                request =
                    GenerationRequest(
                        type = typeOf<String>(),
                        owner = typeOf<User>(),
                        propertyName = "id",
                    ),
                config =
                    base.overlaidBy(
                        other = overlay,
                        rulePrecedence = RulePrecedence.PER_CALL,
                    ),
                seed = 123,
                depth = 0,
            )

        assertEquals("per-call-string", value)
    }

    @Test
    fun `generateValue returns null when the matching rule null probability always applies`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            type<String?>() generates "value" orNullAt 1.0
        }

        val value =
            generateValue(
                request = GenerationRequest(type = typeOf<String?>()),
                config = builder.build(),
                seed = 123,
                depth = 0,
            )

        assertNull(value)
    }

    @Test
    fun `generateValue ignores null probability for non-null requested types`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            type<String>() generates "value" orNullAt 1.0
        }

        val value =
            generateValue(
                request = GenerationRequest(type = typeOf<String>()),
                config = builder.build(),
                seed = 123,
                depth = 0,
            )

        assertEquals("value", value)
    }

    @Test
    fun `generateValue exposes property path in fake context`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            property(User::profile / Profile::nickname) generatesBy {
                path.segments.last().name
            }
        }

        val value =
            generateValue(
                request =
                    GenerationRequest(
                        type = typeOf<String>(),
                        owner = typeOf<Profile>(),
                        propertyName = "nickname",
                        propertyPath = listOf(User::profile, Profile::nickname),
                        pathSegments =
                            listOf(
                                PathRuleSegment(
                                    ownerId = typeOf<User>().toString(),
                                    name = "profile",
                                    valueId = typeOf<Profile>().toString(),
                                ),
                                PathRuleSegment(
                                    ownerId = typeOf<Profile>().toString(),
                                    name = "nickname",
                                    valueId = typeOf<String>().toString(),
                                ),
                            ),
                    ),
                config = builder.build(),
                seed = 123,
                depth = 0,
            )

        assertEquals("nickname", value)
    }

    @Test
    fun `generateValue exposes property context when only property name is provided`() {
        val spec = FakeSpec<User>()

        with(spec) {
            User::id generatesBy {
                "${property?.name}:${property?.nullability}:${path.segments.size}"
            }
        }

        val value =
            generateValue(
                request =
                    GenerationRequest(
                        type = typeOf<String>(),
                        owner = typeOf<User>(),
                        propertyName = "id",
                        propertyPath = listOf(User::id),
                    ),
                config = FiktionConfig(rules = spec.rules),
                seed = 123,
                depth = 0,
            )

        assertEquals("id:NON_NULL:1", value)
    }

    @Test
    fun `generation request defaults final path segment to the requested value type`() {
        val request =
            GenerationRequest(
                type = typeOf<String>(),
                propertyPath = listOf(User::id),
            )

        assertEquals(
            listOf(PathRuleSegment(ownerId = null, name = "id", valueId = typeOf<String>().toString())),
            request.pathSegments,
        )
    }

    @Test
    fun `nullable intermediate path uses non-null normalized type ids`() {
        val builder = DefaultFiktionBuilder()

        with(builder) {
            property(User::optionalProfile / Profile::nickname) generates "nickname"
        }

        val key =
            builder
                .build()
                .rules
                .single()
                .key as RuleKey.Path

        assertEquals(
            listOf(typeOf<Profile>().toString(), typeOf<String>().toString()),
            key.segments.map { segment -> segment.valueId },
        )
    }
}
