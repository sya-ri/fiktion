package dev.s7a.fiktion.detekt

import dev.detekt.test.utils.compileContentForTest
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PsiExtensionsTest {
    @Test
    fun `finds the argument of a containing infix operation`() {
        val call =
            compileContentForTest(
                """
                fun configure() {
                    typeFamily<UserList>() generatesBy
                        {
                            fake(0)
                        }
                }
                """.trimIndent(),
            ).findCall(FiktionCall.TypeFamily)

        val generator =
            call.containingOperationArgument(FiktionOperation.GeneratesBy)
                ?: error("generatesBy argument was not found")

        assertTrue(generator.hasDescendantCall(setOf(FiktionCall.Fake)))
        assertEquals(null, call.containingOperationArgument(FiktionOperation.Using))
    }

    @Test
    fun `finds the argument of a containing regular call operation`() {
        val call =
            compileContentForTest(
                """
                fun configure() {
                    this.typeFamily<UserList>().generatesBy {
                        fake(0)
                    }
                }
                """.trimIndent(),
            ).findCall(FiktionCall.TypeFamily)

        val generator =
            call.containingOperationArgument(FiktionOperation.GeneratesBy)
                ?: error("generatesBy argument was not found")

        assertTrue(generator.hasDescendantCall(setOf(FiktionCall.Fake)))
    }

    @Test
    fun `matches Fiktion infix operations`() {
        val expression =
            compileContentForTest(
                """
                fun configure() {
                    type<String>() generatesBy { "value" }
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtBinaryExpression>()
                .single()

        assertTrue(expression.usesInfixOperation(FiktionOperation.GeneratesBy))
        assertFalse(expression.usesInfixOperation(FiktionOperation.Generates))
        assertFalse(expression.usesInfixOperation(FiktionOperation.Using))
    }

    @Test
    fun `finds descendant calls by callee name`() {
        val generator =
            compileContentForTest(
                """
                fun configure() {
                    typeFamily<UserList>() generatesBy {
                        this.fake(0)
                    }
                }
                """.trimIndent(),
            ).findCall(FiktionCall.TypeFamily).containingOperationArgument(FiktionOperation.GeneratesBy)
                ?: error("generatesBy argument was not found")

        assertTrue(generator.hasDescendantCall(setOf(FiktionCall.Fake)))
        assertFalse(generator.hasDescendantCall(setOf(FiktionCall.TypeFamily)))
    }

    @Test
    fun `detects expressions inside operation arguments`() {
        val references =
            compileContentForTest(
                $$"""
                fun configure() {
                    val outside = seed
                    type<String>() generatesBy {
                        "value-$seed"
                    }
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtNameReferenceExpression>()
                .filter { expression -> expression.text == "seed" }

        assertFalse(references[0].isInsideOperationArgument(FiktionOperation.GeneratesBy))
        assertTrue(references[1].isInsideOperationArgument(FiktionOperation.GeneratesBy))
    }

    @Test
    fun `renders Fiktion config key text`() {
        val file =
            compileContentForTest(
                """
                import dev.s7a.fiktion.FiktionConfig as Config

                fun configure() {
                    FiktionConfig.String.length(8)
                    Config.Int.range(0..10)
                    OtherConfig.String.length(8)
                }
                """.trimIndent(),
            )

        assertEquals(
            "FiktionConfig.String.length",
            file
                .collectDescendantsOfType<KtDotQualifiedExpression>()
                .single { expression -> expression.text == "FiktionConfig.String.length(8)" }
                .fiktionConfigKeyText(),
        )
        assertEquals(
            "Config.Int.range",
            file
                .collectDescendantsOfType<KtDotQualifiedExpression>()
                .single { expression -> expression.text == "Config.Int.range(0..10)" }
                .fiktionConfigKeyText(),
        )
        assertEquals(
            null,
            file
                .collectDescendantsOfType<KtDotQualifiedExpression>()
                .single { expression -> expression.text == "OtherConfig.String.length(8)" }
                .fiktionConfigKeyText(),
        )
    }

    @Test
    fun `defines type-family fake helper calls`() {
        assertEquals(
            setOf(FiktionCall.Fake, FiktionCall.FakeElement, FiktionCall.FakeKey, FiktionCall.FakeValue),
            TYPE_FAMILY_FAKE_HELPER_CALLS,
        )
    }

    @Test
    fun `renders Fiktion rule targets`() {
        val file =
            compileContentForTest(
                """
                fun configure() {
                    type<String>()
                    this.type<Int>()
                    property(User::id)
                    name<String>("email")
                    name("email")
                    User::id
                    this
                }
                """.trimIndent(),
            )

        assertEquals(
            FiktionRuleTargetText(
                sourceText = "type<String>()",
                blockText = "type<String>()",
                requiresInvokeImport = true,
            ),
            file
                .collectDescendantsOfType<KtCallExpression>()
                .single { call -> call.text == "type<String>()" }
                .fiktionRuleTargetText(setOf(FiktionCall.Type)),
        )
        assertEquals(
            FiktionRuleTargetText(
                sourceText = "this.type<Int>()",
                blockText = "this.type<Int>()",
                requiresInvokeImport = true,
            ),
            file
                .collectDescendantsOfType<KtCallExpression>()
                .single { call -> call.text == "type<Int>()" }
                .parent
                .let { expression -> expression as KtExpression }
                .fiktionRuleTargetText(setOf(FiktionCall.Type)),
        )
        assertEquals(
            FiktionRuleTargetText(
                sourceText = "property(User::id)",
                blockText = "property(User::id)",
                requiresInvokeImport = false,
            ),
            file.findCall(FiktionCall.Property).fiktionRuleTargetText(setOf(FiktionCall.Property)),
        )
        assertEquals(
            FiktionRuleTargetText(
                sourceText = "name<String>(\"email\")",
                blockText = "name<String>(\"email\").invoke",
                requiresInvokeImport = true,
            ),
            file
                .collectDescendantsOfType<KtCallExpression>()
                .single { call -> call.text == "name<String>(\"email\")" }
                .fiktionRuleTargetText(setOf(FiktionCall.Name)),
        )
        assertEquals(
            null,
            file
                .collectDescendantsOfType<KtCallExpression>()
                .single { call -> call.text == "name(\"email\")" }
                .fiktionRuleTargetText(setOf(FiktionCall.Name)),
        )
        assertEquals(
            FiktionRuleTargetText(
                sourceText = "User::id",
                blockText = "User::id",
                requiresInvokeImport = false,
            ),
            file
                .collectDescendantsOfType<KtCallableReferenceExpression>()
                .last()
                .fiktionRuleTargetText(calls = emptySet(), allowCallableReference = true),
        )
        assertEquals(
            FiktionRuleTargetText(
                sourceText = "this",
                blockText = "this",
                requiresInvokeImport = false,
            ),
            file
                .collectDescendantsOfType<KtThisExpression>()
                .last { expression -> expression.text == "this" }
                .fiktionRuleTargetText(calls = emptySet(), allowThis = true),
        )
    }

    @Test
    fun `renders Fiktion rule target config declarations`() {
        val statements =
            compileContentForTest(
                """
                fun configure() {
                    type<String>() using FiktionConfig.String.length(8)
                    property(User::id).using(FiktionConfig.String.length(12))
                    User::id using value
                }
                """.trimIndent(),
            ).blockStatements()

        assertEquals(
            FiktionRuleTargetConfig(
                expression = statements[0],
                targetText = "type<String>()",
                configKeyText = "FiktionConfig.String.length",
            ),
            statements[0].ruleTargetConfigText(),
        )
        assertEquals(
            FiktionRuleTargetConfig(
                expression = statements[1],
                targetText = "property(User::id)",
                configKeyText = "FiktionConfig.String.length",
            ),
            statements[1].ruleTargetConfigText(),
        )
        assertEquals(null, statements[2].ruleTargetConfigText())
    }

    @Test
    fun `renders Fiktion generator rule target declarations`() {
        val statements =
            compileContentForTest(
                """
                fun configure() {
                    type<Int>() generates 1
                    property(User::id).generatesBy { "id" }
                    type<Int>() using FiktionConfig.Int.range(0..10)
                }
                """.trimIndent(),
            ).blockStatements()

        assertEquals(
            FiktionGeneratorRuleTarget(expression = statements[0], text = "type<Int>()"),
            statements[0].generatorRuleTargetText(),
        )
        assertEquals(
            FiktionGeneratorRuleTarget(expression = statements[1], text = "property(User::id)"),
            statements[1].generatorRuleTargetText(),
        )
        assertEquals(null, statements[2].generatorRuleTargetText())
    }

    @Test
    fun `returns repeated declarations once per key`() {
        val block =
            compileContentForTest(
                """
                fun configure() {
                    type<Int>() generates 1
                    type<Int>() generates 2
                    type<Int>() generates 3
                    type<String>() generates "value"
                }
                """.trimIndent(),
            ).functionBlock()

        val repeated =
            block.repeatedDeclarations(
                declaration = { statement -> statement.generatorRuleTargetText() },
                key = { target -> target.text },
            )

        assertEquals(listOf("type<Int>()"), repeated.map { target -> target.text })
        assertEquals("type<Int>() generates 2", repeated.single().expression.text)
    }

    @Test
    fun `reads single integer literal arguments`() {
        val calls =
            compileContentForTest(
                """
                fun configure(index: Int) {
                    fake(0)
                    fake(index)
                    fake(1, argumentIndex = 0)
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtCallExpression>()
                .filter { call -> call.hasCallee(FiktionCall.Fake) }

        assertEquals("0", calls[0].singleIntegerLiteralArgumentText())
        assertEquals(0, calls[0].singleIntegerLiteralArgumentValue())
        assertEquals(null, calls[1].singleIntegerLiteralArgumentText())
        assertEquals(null, calls[2].singleIntegerLiteralArgumentText())
    }

    @Test
    fun `finds containing type-family type argument counts`() {
        val calls =
            compileContentForTest(
                """
                fun configure() {
                    typeFamily<Pair<*, *>>() generatesBy {
                        fake(0)
                    }
                    typeFamily<Box<*>>().generatesBy {
                        fake(0)
                    }
                    type<String>() generatesBy {
                        fake<String>()
                    }
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtCallExpression>()
                .filter { call -> call.hasCallee(FiktionCall.Fake) }

        assertEquals(2, calls[0].containingTypeFamilyTypeArgumentCount())
        assertEquals(1, calls[1].containingTypeFamilyTypeArgumentCount())
        assertEquals(null, calls[2].containingTypeFamilyTypeArgumentCount())
    }

    @Test
    fun `detects expressions inside loops`() {
        val references =
            compileContentForTest(
                """
                fun configure(values: List<Int>) {
                    val outside = value

                    for (value in values) {
                        val insideFor = value
                    }

                    values.forEach { value ->
                        val insideForEach = value
                    }
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtNameReferenceExpression>()
                .filter { expression -> expression.text == "value" }

        assertFalse(references[0].isInsideLoop())
        assertTrue(references[1].isInsideLoop())
        assertTrue(references[2].isInsideLoop())
    }

    @Test
    fun `detects lambda body blocks passed to a call`() {
        val blocks =
            compileContentForTest(
                """
                fun test() {
                    fake<User> {
                        withSeed(1)
                    }
                    run {
                        withSeed(2)
                    }
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtBlockExpression>()
                .filter { block -> block.statements.any { statement -> statement.isWithSeedStatement() } }

        assertTrue(blocks[0].isLambdaBodyOfCall(FiktionCall.Fake))
        assertFalse(blocks[1].isLambdaBodyOfCall(FiktionCall.Fake))
    }

    @Test
    fun `detects expressions inside Fiktion configuration scopes`() {
        val calls =
            compileContentForTest(
                """
                fun test() {
                    Fiktion {
                        type<String>() generates "value"
                    }
                    Fiktion.configure {
                        type<Int>() generates 1
                    }
                    helper {
                        type<Boolean>() generates false
                    }
                }
                """.trimIndent(),
            ).collectDescendantsOfType<KtCallExpression>()
                .filter { call -> call.hasCallee(FiktionCall.Type) }

        assertTrue(calls[0].isInsideFiktionConfigurationScope())
        assertTrue(calls[1].isInsideFiktionConfigurationScope())
        assertFalse(calls[2].isInsideFiktionConfigurationScope())
    }
}

private fun KtFile.findCall(callee: FiktionCall): KtCallExpression =
    collectDescendantsOfType<KtCallExpression>()
        .single { call -> call.hasCallee(callee) }

private fun KtFile.functionBlock() =
    collectDescendantsOfType<KtNamedFunction>()
        .single()
        .bodyBlockExpression
        ?: error("Function body block was not found")

private fun KtFile.blockStatements(): List<KtExpression> = functionBlock().statements
