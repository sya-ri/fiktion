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
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Reports type-family container generators that can use configureCollection or configureMap.
 */
public class PreferContainerConfigureHelpers(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer configureCollection or configureMap for container materialization rules.",
    ) {
    private val containerMaterializationCalls: Set<String> by config(DEFAULT_CONTAINER_MATERIALIZATION_CALLS.toList()) { calls ->
        calls.toSet()
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return
        if (!expression.usesInfixOperation(FiktionOperation.GeneratesBy)) return
        val targetText = expression.left?.typeFamilyTargetText() ?: return
        val lambda = expression.right as? KtLambdaExpression ?: return
        val helper = lambda.containerHelperKind(containerMaterializationCalls) ?: return
        val replacement = if (helper == ContainerHelperKind.Map) "configureMap" else "configureCollection"

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `$targetText generatesBy` when only materializing generated container parts.",
            ),
        )
    }
}

private enum class ContainerHelperKind {
    Collection,
    Map,
}

private fun KtLambdaExpression.containerHelperKind(containerMaterializationCalls: Set<String>): ContainerHelperKind? {
    val statement = bodyExpression?.statements?.singleOrNull() ?: return null
    if (!statement.onlyMaterializesContainerParts(containerMaterializationCalls)) return null

    val hasKeyOrValue = statement.hasDescendantCall(setOf(FiktionCall.FakeKey, FiktionCall.FakeValue))
    val hasElement = statement.hasDescendantCall(setOf(FiktionCall.FakeElement))
    return when {
        hasKeyOrValue -> ContainerHelperKind.Map
        hasElement -> ContainerHelperKind.Collection
        else -> null
    }
}

private fun KtExpression.onlyMaterializesContainerParts(containerMaterializationCalls: Set<String>): Boolean {
    val outerCall = this as? KtCallExpression
    return collectDescendantsOfType<KtCallExpression>().all { call ->
        call == outerCall ||
            call.hasCallee(CONTAINER_PART_FAKE_HELPER_CALLS) ||
            call.calleeExpression?.text in containerMaterializationCalls
    }
}

private fun KtExpression.typeFamilyTargetText(): String? =
    when (this) {
        is KtCallExpression -> text.takeIf { hasCallee(FiktionCall.TypeFamily) }
        is KtDotQualifiedExpression -> text.takeIf { (selectorExpression as? KtCallExpression)?.hasCallee(FiktionCall.TypeFamily) == true }
        else -> null
    }

private val CONTAINER_PART_FAKE_HELPER_CALLS = setOf(FiktionCall.FakeElement, FiktionCall.FakeKey, FiktionCall.FakeValue)

private val DEFAULT_CONTAINER_MATERIALIZATION_CALLS =
    setOf(
        "Array",
        "List",
        "MutableList",
        "buildList",
        "listOf",
        "mapOf",
        "mutableListOf",
        "mutableMapOf",
        "mutableSetOf",
        "setOf",
        "toList",
        "toMap",
        "toMutableList",
        "toMutableMap",
        "toMutableSet",
        "toSet",
    )
