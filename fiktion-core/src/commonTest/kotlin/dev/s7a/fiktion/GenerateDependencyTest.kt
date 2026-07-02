package dev.s7a.fiktion

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateDependencyTest {
    @Test
    fun `dependent property receives an earlier generated property value`() {
        val fiktion =
            Fiktion {
                register(dependentUserMetadata())
                DependentUser::id generates "user-1"
                DependentUser::email.dependsOn(DependentUser::id) generatesBy { id ->
                    "$id@example.test"
                }
            }

        assertEquals(DependentUser(id = "user-1", email = "user-1@example.test"), fiktion.fake<DependentUser>())
    }

    @Test
    fun `dependent property receives multiple dependency values in declaration order`() {
        val fiktion =
            Fiktion {
                register(personNameMetadata())
                PersonName::firstName generates "Ada"
                PersonName::lastName generates "Lovelace"
                PersonName::displayName.dependsOn(PersonName::firstName, PersonName::lastName) generatesBy { first, last ->
                    "$first $last"
                }
            }

        assertEquals(PersonName(firstName = "Ada", lastName = "Lovelace", displayName = "Ada Lovelace"), fiktion.fake<PersonName>())
    }

    @Test
    fun `multiple dependent properties can share the same dependency value`() {
        val fiktion =
            Fiktion {
                register(sharedDependencyMetadata())
                SharedDependency::id generates "user-1"
                SharedDependency::label.dependsOn(SharedDependency::id) generatesBy { id ->
                    "label:$id"
                }
                SharedDependency::slug.dependsOn(SharedDependency::id) generatesBy { id ->
                    "slug:$id"
                }
            }

        assertEquals(SharedDependency(id = "user-1", label = "label:user-1", slug = "slug:user-1"), fiktion.fake<SharedDependency>())
    }

    @Test
    fun `dependent property supports typed arity twenty two`() {
        val fiktion =
            Fiktion {
                register(manyDependencyMetadata())
                ManyDependency::p1 generates "v1"
                ManyDependency::p2 generates "v2"
                ManyDependency::p3 generates "v3"
                ManyDependency::p4 generates "v4"
                ManyDependency::p5 generates "v5"
                ManyDependency::p6 generates "v6"
                ManyDependency::p7 generates "v7"
                ManyDependency::p8 generates "v8"
                ManyDependency::p9 generates "v9"
                ManyDependency::p10 generates "v10"
                ManyDependency::p11 generates "v11"
                ManyDependency::p12 generates "v12"
                ManyDependency::p13 generates "v13"
                ManyDependency::p14 generates "v14"
                ManyDependency::p15 generates "v15"
                ManyDependency::p16 generates "v16"
                ManyDependency::p17 generates "v17"
                ManyDependency::p18 generates "v18"
                ManyDependency::p19 generates "v19"
                ManyDependency::p20 generates "v20"
                ManyDependency::p21 generates "v21"
                ManyDependency::p22 generates "v22"
                ManyDependency::result.dependsOn(
                    ManyDependency::p1,
                    ManyDependency::p2,
                    ManyDependency::p3,
                    ManyDependency::p4,
                    ManyDependency::p5,
                    ManyDependency::p6,
                    ManyDependency::p7,
                    ManyDependency::p8,
                    ManyDependency::p9,
                    ManyDependency::p10,
                    ManyDependency::p11,
                    ManyDependency::p12,
                    ManyDependency::p13,
                    ManyDependency::p14,
                    ManyDependency::p15,
                    ManyDependency::p16,
                    ManyDependency::p17,
                    ManyDependency::p18,
                    ManyDependency::p19,
                    ManyDependency::p20,
                    ManyDependency::p21,
                    ManyDependency::p22,
                ) generatesBy { p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16, p17, p18, p19, p20, p21, p22 ->
                    listOf(
                        p1,
                        p2,
                        p3,
                        p4,
                        p5,
                        p6,
                        p7,
                        p8,
                        p9,
                        p10,
                        p11,
                        p12,
                        p13,
                        p14,
                        p15,
                        p16,
                        p17,
                        p18,
                        p19,
                        p20,
                        p21,
                        p22,
                    ).joinToString("|")
                }
            }

        assertEquals((1..22).joinToString("|") { index -> "v$index" }, fiktion.fake<ManyDependency>().result)
    }

    @Test
    fun `dependent property supports untyped vararg dependencies`() {
        val dependencies = arrayOf(PersonName::firstName, PersonName::lastName)
        val fiktion =
            Fiktion {
                register(personNameMetadata())
                PersonName::firstName generates "Grace"
                PersonName::lastName generates "Hopper"
                PersonName::displayName.dependsOn(*dependencies) generatesBy { values ->
                    values.joinToString(" ")
                }
            }

        assertEquals(PersonName(firstName = "Grace", lastName = "Hopper", displayName = "Grace Hopper"), fiktion.fake<PersonName>())
    }

    @Test
    fun `dependent property receives null dependency values`() {
        val fiktion =
            Fiktion {
                register(nullableDependencyMetadata())
                NullableDependency::id generates null
                NullableDependency::label.dependsOn(NullableDependency::id) generatesBy { id ->
                    id ?: "missing"
                }
            }

        assertEquals(NullableDependency(id = null, label = "missing"), fiktion.fake<NullableDependency>())
    }

    @Test
    fun `dependent property fails when metadata dependency type does not match declaration`() {
        val fiktion =
            Fiktion {
                register(mismatchedDependencyMetadata())
                MismatchedDependency::label.dependsOn(MismatchedDependency::id) generatesBy { id ->
                    id
                }
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<MismatchedDependency>()
            }

        assertTrue(error.hasMessageContaining("dependency property type does not match"))
    }

    @Test
    fun `dependent property fails when dependency used constructor default`() {
        val fiktion =
            Fiktion {
                register(defaultDependencyMetadata())
                DefaultDependency::id generates default
                DefaultDependency::label.dependsOn(DefaultDependency::id) generatesBy { id ->
                    id
                }
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<DefaultDependency>()
            }

        assertTrue(error.hasMessageContaining("used a constructor default value"))
    }

    @Test
    fun `dependent property fails when dependency is generated later`() {
        val fiktion =
            Fiktion {
                register(lateDependencyMetadata())
                LateDependency::label.dependsOn(LateDependency::id) generatesBy { id ->
                    id
                }
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<LateDependency>()
            }

        assertTrue(error.hasMessageContaining("must be generated before"))
    }

    @Test
    fun `per-call dependent property rules override instance rules`() {
        val fiktion =
            Fiktion {
                register(dependentUserMetadata())
                DependentUser::id generates "instance"
                DependentUser::email.dependsOn(DependentUser::id) generatesBy { id ->
                    "$id@instance.test"
                }
            }

        val user =
            fiktion.fake<DependentUser> {
                DependentUser::id generates "per-call"
                DependentUser::email.dependsOn(DependentUser::id) generatesBy { id ->
                    "$id@per-call.test"
                }
            }

        assertEquals(DependentUser(id = "per-call", email = "per-call@per-call.test"), user)
    }

    @Test
    fun `dependent property rules override type fallback for the target property`() {
        val fiktion =
            Fiktion {
                register(dependentUserMetadata())
                type<String>() generates "fallback"
                DependentUser::id generates "user-1"
                DependentUser::email.dependsOn(DependentUser::id) generatesBy { id ->
                    "$id@example.test"
                }
            }

        assertEquals(DependentUser(id = "user-1", email = "user-1@example.test"), fiktion.fake<DependentUser>())
    }

    @Test
    fun `dependent property rules stay scoped to each recursive object frame`() {
        val fiktion =
            Fiktion {
                register(recursiveNodeMetadata())
                RecursiveNode::id generatesBy { "id-$depth" }
                RecursiveNode::label.dependsOn(RecursiveNode::id) generatesBy { id ->
                    "$id:$depth"
                }
                RecursiveNode::child generatesBy {
                    if (depth >= 3) {
                        null
                    } else {
                        val context = this as DefaultFakeContext
                        generateValue(
                            request = GenerationRequest(type = typeOf<RecursiveNode>()),
                            config = context.config,
                            seed = seed.childSeed(0),
                            depth = depth,
                        ) as RecursiveNode
                    }
                }
            }

        val root = fiktion.fake<RecursiveNode>(seed = 123)
        val child = root.child ?: error("Expected a recursive child")

        assertEquals("id-1:1", root.label)
        assertEquals("id-2:2", child.label)
        assertNotEquals(root.id, child.id)
    }

    @Test
    fun `single dependency property stays scoped to each recursive object frame`() {
        val fiktion =
            Fiktion {
                register(unaryRecursiveDependencyMetadata())
                UnaryRecursiveDependency::id generatesBy { "id-$depth" }
                UnaryRecursiveDependency::label.dependsOn(UnaryRecursiveDependency::id) generatesBy { id ->
                    "$id:$depth"
                }
                UnaryRecursiveDependency::child generatesBy {
                    if (depth >= 3) {
                        null
                    } else {
                        val context = this as DefaultFakeContext
                        generateValue(
                            request = GenerationRequest(type = typeOf<UnaryRecursiveDependency>()),
                            config = context.config,
                            seed = seed.childSeed(0),
                            depth = depth,
                        ) as UnaryRecursiveDependency
                    }
                }
            }

        val root = fiktion.fake<UnaryRecursiveDependency>(seed = 123)
        val child = root.child ?: error("Expected a recursive child")
        val grandchild = child.child ?: error("Expected a recursive grandchild")

        assertEquals("id-1:1", root.label)
        assertEquals("id-2:2", child.label)
        assertEquals("id-3:3", grandchild.label)
    }

    @Test
    fun `two dependency property stays scoped to each recursive object frame`() {
        val fiktion =
            Fiktion {
                register(binaryRecursiveDependencyMetadata())
                BinaryRecursiveDependency::first generatesBy { "first-$depth" }
                BinaryRecursiveDependency::second generatesBy { "second-$depth" }
                BinaryRecursiveDependency::label
                    .dependsOn(BinaryRecursiveDependency::first, BinaryRecursiveDependency::second)
                    .generatesBy { first, second ->
                        "$first:$second:$depth"
                    }
                BinaryRecursiveDependency::child generatesBy {
                    if (depth >= 3) {
                        null
                    } else {
                        val context = this as DefaultFakeContext
                        generateValue(
                            request = GenerationRequest(type = typeOf<BinaryRecursiveDependency>()),
                            config = context.config,
                            seed = seed.childSeed(0),
                            depth = depth,
                        ) as BinaryRecursiveDependency
                    }
                }
            }

        val root = fiktion.fake<BinaryRecursiveDependency>(seed = 123)
        val child = root.child ?: error("Expected a recursive child")
        val grandchild = child.child ?: error("Expected a recursive grandchild")

        assertEquals("first-1:second-1:1", root.label)
        assertEquals("first-2:second-2:2", child.label)
        assertEquals("first-3:second-3:3", grandchild.label)
    }

    @Test
    fun `three dependency property stays scoped to each recursive object frame`() {
        val fiktion =
            Fiktion {
                register(ternaryRecursiveDependencyMetadata())
                TernaryRecursiveDependency::first generatesBy { "first-$depth" }
                TernaryRecursiveDependency::second generatesBy { "second-$depth" }
                TernaryRecursiveDependency::third generatesBy { "third-$depth" }
                TernaryRecursiveDependency::label
                    .dependsOn(
                        TernaryRecursiveDependency::first,
                        TernaryRecursiveDependency::second,
                        TernaryRecursiveDependency::third,
                    ).generatesBy { first, second, third ->
                        "$first:$second:$third:$depth"
                    }
                TernaryRecursiveDependency::child generatesBy {
                    if (depth >= 3) {
                        null
                    } else {
                        val context = this as DefaultFakeContext
                        generateValue(
                            request = GenerationRequest(type = typeOf<TernaryRecursiveDependency>()),
                            config = context.config,
                            seed = seed.childSeed(0),
                            depth = depth,
                        ) as TernaryRecursiveDependency
                    }
                }
            }

        val root = fiktion.fake<TernaryRecursiveDependency>(seed = 123)
        val child = root.child ?: error("Expected a recursive child")
        val grandchild = child.child ?: error("Expected a recursive grandchild")

        assertEquals("first-1:second-1:third-1:1", root.label)
        assertEquals("first-2:second-2:third-2:2", child.label)
        assertEquals("first-3:second-3:third-3:3", grandchild.label)
    }

    @Test
    fun `recursive dependency order cycle fails before recursive generation`() {
        val fiktion =
            Fiktion {
                register(recursiveOrderCycleMetadata())
                RecursiveOrderCycle::child.dependsOn(RecursiveOrderCycle::label) generatesBy { _ ->
                    null
                }
                RecursiveOrderCycle::label generates "label"
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<RecursiveOrderCycle>()
            }

        assertTrue(error.hasMessageContaining("must be generated before"))
    }

    private fun dependentUserMetadata(): FiktionObjectMetadata<DependentUser> =
        FiktionObjectMetadata(
            type = typeOf<DependentUser>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "email", type = typeOf<String>()),
                ),
        ) { values ->
            DependentUser(
                id = values[0].valueOrDefault(defaultValue = null) as String,
                email = values[1].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun personNameMetadata(): FiktionObjectMetadata<PersonName> =
        FiktionObjectMetadata(
            type = typeOf<PersonName>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "firstName", type = typeOf<String>()),
                    FiktionObjectProperty(name = "lastName", type = typeOf<String>()),
                    FiktionObjectProperty(name = "displayName", type = typeOf<String>()),
                ),
        ) { values ->
            PersonName(
                firstName = values[0].valueOrDefault(defaultValue = null) as String,
                lastName = values[1].valueOrDefault(defaultValue = null) as String,
                displayName = values[2].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun sharedDependencyMetadata(): FiktionObjectMetadata<SharedDependency> =
        FiktionObjectMetadata(
            type = typeOf<SharedDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                    FiktionObjectProperty(name = "slug", type = typeOf<String>()),
                ),
        ) { values ->
            SharedDependency(
                id = values[0].valueOrDefault(defaultValue = null) as String,
                label = values[1].valueOrDefault(defaultValue = null) as String,
                slug = values[2].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun manyDependencyMetadata(): FiktionObjectMetadata<ManyDependency> =
        FiktionObjectMetadata(
            type = typeOf<ManyDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "p1", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p2", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p3", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p4", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p5", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p6", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p7", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p8", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p9", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p10", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p11", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p12", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p13", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p14", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p15", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p16", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p17", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p18", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p19", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p20", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p21", type = typeOf<String>()),
                    FiktionObjectProperty(name = "p22", type = typeOf<String>()),
                    FiktionObjectProperty(name = "result", type = typeOf<String>()),
                ),
        ) { values ->
            ManyDependency(
                p1 = values[0].valueOrDefault(defaultValue = null) as String,
                p2 = values[1].valueOrDefault(defaultValue = null) as String,
                p3 = values[2].valueOrDefault(defaultValue = null) as String,
                p4 = values[3].valueOrDefault(defaultValue = null) as String,
                p5 = values[4].valueOrDefault(defaultValue = null) as String,
                p6 = values[5].valueOrDefault(defaultValue = null) as String,
                p7 = values[6].valueOrDefault(defaultValue = null) as String,
                p8 = values[7].valueOrDefault(defaultValue = null) as String,
                p9 = values[8].valueOrDefault(defaultValue = null) as String,
                p10 = values[9].valueOrDefault(defaultValue = null) as String,
                p11 = values[10].valueOrDefault(defaultValue = null) as String,
                p12 = values[11].valueOrDefault(defaultValue = null) as String,
                p13 = values[12].valueOrDefault(defaultValue = null) as String,
                p14 = values[13].valueOrDefault(defaultValue = null) as String,
                p15 = values[14].valueOrDefault(defaultValue = null) as String,
                p16 = values[15].valueOrDefault(defaultValue = null) as String,
                p17 = values[16].valueOrDefault(defaultValue = null) as String,
                p18 = values[17].valueOrDefault(defaultValue = null) as String,
                p19 = values[18].valueOrDefault(defaultValue = null) as String,
                p20 = values[19].valueOrDefault(defaultValue = null) as String,
                p21 = values[20].valueOrDefault(defaultValue = null) as String,
                p22 = values[21].valueOrDefault(defaultValue = null) as String,
                result = values[22].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun nullableDependencyMetadata(): FiktionObjectMetadata<NullableDependency> =
        FiktionObjectMetadata(
            type = typeOf<NullableDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String?>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                ),
        ) { values ->
            NullableDependency(
                id = values[0].valueOrDefault(defaultValue = null) as String?,
                label = values[1].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun mismatchedDependencyMetadata(): FiktionObjectMetadata<MismatchedDependency> =
        FiktionObjectMetadata(
            type = typeOf<MismatchedDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<Int>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                ),
        ) { values ->
            MismatchedDependency(
                id = values[0].valueOrDefault(defaultValue = null).toString(),
                label = values[1].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun defaultDependencyMetadata(): FiktionObjectMetadata<DefaultDependency> =
        FiktionObjectMetadata(
            type = typeOf<DefaultDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>(), hasDefault = true),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                ),
        ) { values ->
            DefaultDependency(
                id = values[0].valueOrDefault(defaultValue = "default") as String,
                label = values[1].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun lateDependencyMetadata(): FiktionObjectMetadata<LateDependency> =
        FiktionObjectMetadata(
            type = typeOf<LateDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                ),
        ) { values ->
            LateDependency(
                label = values[0].valueOrDefault(defaultValue = null) as String,
                id = values[1].valueOrDefault(defaultValue = null) as String,
            )
        }

    private fun recursiveNodeMetadata(): FiktionObjectMetadata<RecursiveNode> =
        FiktionObjectMetadata(
            type = typeOf<RecursiveNode>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                    FiktionObjectProperty(name = "child", type = typeOf<RecursiveNode?>()),
                ),
        ) { values ->
            RecursiveNode(
                id = values[0].valueOrDefault(defaultValue = null) as String,
                label = values[1].valueOrDefault(defaultValue = null) as String,
                child = values[2].valueOrDefault(defaultValue = null) as RecursiveNode?,
            )
        }

    private fun unaryRecursiveDependencyMetadata(): FiktionObjectMetadata<UnaryRecursiveDependency> =
        FiktionObjectMetadata(
            type = typeOf<UnaryRecursiveDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "id", type = typeOf<String>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                    FiktionObjectProperty(name = "child", type = typeOf<UnaryRecursiveDependency?>()),
                ),
        ) { values ->
            UnaryRecursiveDependency(
                id = values[0].valueOrDefault(defaultValue = null) as String,
                label = values[1].valueOrDefault(defaultValue = null) as String,
                child = values[2].valueOrDefault(defaultValue = null) as UnaryRecursiveDependency?,
            )
        }

    private fun binaryRecursiveDependencyMetadata(): FiktionObjectMetadata<BinaryRecursiveDependency> =
        FiktionObjectMetadata(
            type = typeOf<BinaryRecursiveDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "first", type = typeOf<String>()),
                    FiktionObjectProperty(name = "second", type = typeOf<String>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                    FiktionObjectProperty(name = "child", type = typeOf<BinaryRecursiveDependency?>()),
                ),
        ) { values ->
            BinaryRecursiveDependency(
                first = values[0].valueOrDefault(defaultValue = null) as String,
                second = values[1].valueOrDefault(defaultValue = null) as String,
                label = values[2].valueOrDefault(defaultValue = null) as String,
                child = values[3].valueOrDefault(defaultValue = null) as BinaryRecursiveDependency?,
            )
        }

    private fun ternaryRecursiveDependencyMetadata(): FiktionObjectMetadata<TernaryRecursiveDependency> =
        FiktionObjectMetadata(
            type = typeOf<TernaryRecursiveDependency>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "first", type = typeOf<String>()),
                    FiktionObjectProperty(name = "second", type = typeOf<String>()),
                    FiktionObjectProperty(name = "third", type = typeOf<String>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                    FiktionObjectProperty(name = "child", type = typeOf<TernaryRecursiveDependency?>()),
                ),
        ) { values ->
            TernaryRecursiveDependency(
                first = values[0].valueOrDefault(defaultValue = null) as String,
                second = values[1].valueOrDefault(defaultValue = null) as String,
                third = values[2].valueOrDefault(defaultValue = null) as String,
                label = values[3].valueOrDefault(defaultValue = null) as String,
                child = values[4].valueOrDefault(defaultValue = null) as TernaryRecursiveDependency?,
            )
        }

    private fun recursiveOrderCycleMetadata(): FiktionObjectMetadata<RecursiveOrderCycle> =
        FiktionObjectMetadata(
            type = typeOf<RecursiveOrderCycle>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "child", type = typeOf<RecursiveOrderCycle?>()),
                    FiktionObjectProperty(name = "label", type = typeOf<String>()),
                ),
        ) { values ->
            RecursiveOrderCycle(
                child = values[0].valueOrDefault(defaultValue = null) as RecursiveOrderCycle?,
                label = values[1].valueOrDefault(defaultValue = null) as String,
            )
        }
}

private data class DependentUser(
    val id: String,
    val email: String,
)

private data class PersonName(
    val firstName: String,
    val lastName: String,
    val displayName: String,
)

private data class SharedDependency(
    val id: String,
    val label: String,
    val slug: String,
)

private data class ManyDependency(
    val p1: String,
    val p2: String,
    val p3: String,
    val p4: String,
    val p5: String,
    val p6: String,
    val p7: String,
    val p8: String,
    val p9: String,
    val p10: String,
    val p11: String,
    val p12: String,
    val p13: String,
    val p14: String,
    val p15: String,
    val p16: String,
    val p17: String,
    val p18: String,
    val p19: String,
    val p20: String,
    val p21: String,
    val p22: String,
    val result: String,
)

private data class NullableDependency(
    val id: String?,
    val label: String,
)

private data class MismatchedDependency(
    val id: String,
    val label: String,
)

private data class DefaultDependency(
    val id: String = "default",
    val label: String,
)

private data class LateDependency(
    val label: String,
    val id: String,
)

private data class RecursiveNode(
    val id: String,
    val label: String,
    val child: RecursiveNode?,
)

private data class UnaryRecursiveDependency(
    val id: String,
    val label: String,
    val child: UnaryRecursiveDependency?,
)

private data class BinaryRecursiveDependency(
    val first: String,
    val second: String,
    val label: String,
    val child: BinaryRecursiveDependency?,
)

private data class TernaryRecursiveDependency(
    val first: String,
    val second: String,
    val third: String,
    val label: String,
    val child: TernaryRecursiveDependency?,
)

private data class RecursiveOrderCycle(
    val child: RecursiveOrderCycle?,
    val label: String,
)

private fun Throwable.hasMessageContaining(value: String): Boolean =
    generateSequence(this) { error -> error.cause }
        .any { error -> error.message.orEmpty().contains(value) }
