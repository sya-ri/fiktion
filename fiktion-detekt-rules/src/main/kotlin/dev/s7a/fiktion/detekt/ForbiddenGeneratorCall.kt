package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports configured calls inside generator lambdas.
 */
public class ForbiddenGeneratorCall(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid configured calls inside Fiktion generators.",
    ) {
    private val calls: Set<String> by config(DEFAULT_FORBIDDEN_GENERATOR_CALLS.toList()) { calls -> calls.toSet() }
    private val matcher: GeneratorCallMatcher
        get() = GeneratorCallMatcher(calls)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isInsideOperationArgument(FiktionOperation.GeneratesBy)) return
        val qualifiedParent = expression.parent as? KtDotQualifiedExpression
        if (qualifiedParent?.selectorExpression == expression && matcher.matches(qualifiedParent)) return
        if (!matcher.matches(expression)) return

        reportForbiddenCall(expression, expression.calleeExpression?.text ?: expression.text)
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        if (!expression.isInsideOperationArgument(FiktionOperation.GeneratesBy)) return
        if (expression.isReceiverOfDotQualifiedExpression()) return
        if (!matcher.matches(expression)) return

        reportForbiddenCall(expression, expression.qualifiedCallText() ?: expression.text)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.isInsideOperationArgument(FiktionOperation.GeneratesBy)) return
        val operation = expression.operationReference.text
        if (operation !in calls) return

        reportForbiddenCall(expression, operation)
    }

    private fun reportForbiddenCall(
        expression: KtExpression,
        callText: String,
    ) {
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Do not call `$callText` inside a Fiktion generator.",
            ),
        )
    }
}

private val DEFAULT_FORBIDDEN_GENERATOR_CALLS =
    setOf(
        "Clock.System.now",
        "Date",
        "File.readBytes",
        "File.readText",
        "Files.readAllBytes",
        "Files.readString",
        "Instant.now",
        "LocalDate.now",
        "LocalDateTime.now",
        "ProcessBuilder",
        "Random.nextBoolean",
        "Random.nextBytes",
        "Random.nextDouble",
        "Random.nextFloat",
        "Random.nextInt",
        "Random.nextLong",
        "Runtime.getRuntime",
        "System.currentTimeMillis",
        "System.getenv",
        "System.getProperty",
        "System.nanoTime",
        "Thread.sleep",
        "UUID.randomUUID",
        "assertContains",
        "assertContentEquals",
        "assertEquals",
        "assertFails",
        "assertFailsWith",
        "assertFalse",
        "assertIs",
        "assertIsNot",
        "assertNotEquals",
        "assertNotNull",
        "assertNotSame",
        "assertNull",
        "assertSame",
        "assertTrue",
        "fail",
        "java.io.File.readBytes",
        "java.io.File.readText",
        "java.lang.System.currentTimeMillis",
        "java.lang.System.getenv",
        "java.lang.System.getProperty",
        "java.lang.System.nanoTime",
        "java.lang.Thread.sleep",
        "java.nio.file.Files.readAllBytes",
        "java.nio.file.Files.readString",
        "java.time.LocalDate.now",
        "java.time.LocalDateTime.now",
        "java.util.Date",
        "java.util.UUID.randomUUID",
        "java.time.Instant.now",
        "kotlin.random.Random.nextBoolean",
        "kotlin.random.Random.nextBytes",
        "kotlin.random.Random.nextDouble",
        "kotlin.random.Random.nextFloat",
        "kotlin.random.Random.nextInt",
        "kotlin.random.Random.nextLong",
        "kotlin.time.Clock.System.now",
        "kotlin.time.Instant.now",
        "kotlinx.datetime.Clock.System.now",
        "kotlinx.datetime.Instant.now",
    )
