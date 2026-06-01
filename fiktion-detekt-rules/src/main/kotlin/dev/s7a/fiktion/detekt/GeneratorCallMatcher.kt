package dev.s7a.fiktion.detekt

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

/**
 * Matches calls inside Fiktion generator bodies by source-level call text.
 */
internal class GeneratorCallMatcher(
    private val calls: Set<String>,
) {
    fun matches(expression: KtCallExpression): Boolean = expression.calleeExpression?.text in calls

    fun matches(expression: KtDotQualifiedExpression): Boolean = expression.qualifiedCallText() in calls
}

internal fun KtDotQualifiedExpression.qualifiedCallText(): String? {
    val selectorCall = selectorExpression as? KtCallExpression ?: return null
    val callee = selectorCall.calleeExpression?.text ?: return null
    val receiver = receiverExpression.text.substringBefore("(")
    return "$receiver.$callee"
}
