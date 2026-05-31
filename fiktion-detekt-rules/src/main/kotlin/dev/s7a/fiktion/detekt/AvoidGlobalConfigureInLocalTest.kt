package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Reports global Fiktion.configure calls inside local test functions.
 */
public class AvoidGlobalConfigureInLocalTest(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid global Fiktion.configure calls inside local test functions.",
    ) {
    private val testAnnotations: Set<String> by config(DEFAULT_TEST_ANNOTATIONS.toList()) { values -> values.toSet() }
    private val testNamePrefixes: Set<String> by config(DEFAULT_TEST_NAME_PREFIXES.toList()) { values -> values.toSet() }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isFiktionConfigureCall()) return
        val function = expression.getStrictParentOfType<KtNamedFunction>() ?: return
        if (!function.isConfiguredTestFunction(testAnnotations, testNamePrefixes)) return

        report(
            Finding(
                entity = Entity.from(expression.parent as? KtDotQualifiedExpression ?: expression),
                message =
                    "Avoid `Fiktion.configure` inside a local test function; " +
                        "prefer per-call `fake { ... }` or a scoped `Fiktion { ... }` instance.",
            ),
        )
    }
}

private fun KtNamedFunction.isConfiguredTestFunction(
    annotations: Set<String>,
    namePrefixes: Set<String>,
): Boolean {
    val functionName = name.orEmpty()
    if (namePrefixes.any { prefix -> functionName.startsWith(prefix) }) return true
    return annotationEntries.any { annotation ->
        val shortName = annotation.shortName?.asString()
        val text = annotation.typeReference?.text
        shortName in annotations || text in annotations
    }
}

private val DEFAULT_TEST_ANNOTATIONS = setOf("Test", "kotlin.test.Test", "org.junit.Test")
private val DEFAULT_TEST_NAME_PREFIXES = setOf("test")
