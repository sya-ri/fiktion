package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

/**
 * Reports random instances inside generator lambdas.
 */
public class AvoidRandomInstanceInGenerator(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid creating or referencing Random instances inside Fiktion generators.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
        super.visitDotQualifiedExpression(expression)

        if (!expression.isInsideOperationArgument(FiktionOperation.GeneratesBy)) return
        if (expression.isReceiverOfRandomDefaultReference()) return
        val replacement = expression.fakeContextRandomReplacement() ?: return
        reportReplacement(expression = expression, replacement = replacement)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isInsideOperationArgument(FiktionOperation.GeneratesBy)) return
        if (expression.isSelectorOfKotlinRandomConstructorReference()) return
        val replacement = expression.fakeContextRandomReplacement() ?: return
        reportReplacement(expression = expression, replacement = replacement)
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }

    private fun reportReplacement(
        expression: KtExpression,
        replacement: String,
    ) {
        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}` inside a Fiktion generator.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
        }
    }
}

private fun KtExpression.fakeContextRandomReplacement(): String? =
    when (this) {
        is KtCallExpression -> {
            FAKE_CONTEXT_RANDOM_NAME.takeIf { isKotlinRandomConstructor() }
        }

        is KtDotQualifiedExpression -> {
            FAKE_CONTEXT_RANDOM_NAME.takeIf {
                text == KOTLIN_RANDOM_FQ_NAME || isRandomDefaultReference() || isKotlinRandomConstructorReference()
            }
        }

        else -> {
            null
        }
    }

private fun KtDotQualifiedExpression.isKotlinRandomConstructorReference(): Boolean {
    val selector = selectorExpression as? KtCallExpression ?: return false
    return receiverExpression.text == KOTLIN_RANDOM_PACKAGE_NAME && selector.calleeExpression?.text == KOTLIN_RANDOM_NAME
}

private fun KtCallExpression.isSelectorOfKotlinRandomConstructorReference(): Boolean {
    val qualified = parent as? KtDotQualifiedExpression ?: return false
    return qualified.selectorExpression == this &&
        qualified.receiverExpression.text == KOTLIN_RANDOM_PACKAGE_NAME &&
        calleeExpression?.text == KOTLIN_RANDOM_NAME
}

private fun KtDotQualifiedExpression.isRandomDefaultReference(): Boolean {
    val selector = selectorExpression as? KtNameReferenceExpression ?: return false
    if (selector.text != KOTLIN_RANDOM_DEFAULT_NAME) return false
    return receiverExpression.text == KOTLIN_RANDOM_NAME || receiverExpression.text == KOTLIN_RANDOM_FQ_NAME
}

private fun KtExpression.isReceiverOfRandomDefaultReference(): Boolean {
    val parent = parent as? KtDotQualifiedExpression ?: return false
    if (parent.receiverExpression != this) return false
    return (parent.selectorExpression as? KtNameReferenceExpression)?.text == KOTLIN_RANDOM_DEFAULT_NAME
}

private fun KtCallExpression.isKotlinRandomConstructor(): Boolean =
    calleeExpression?.text == KOTLIN_RANDOM_NAME || calleeExpression?.text == KOTLIN_RANDOM_FQ_NAME

private const val FAKE_CONTEXT_RANDOM_NAME = "random"
private const val KOTLIN_RANDOM_DEFAULT_NAME = "Default"
private const val KOTLIN_RANDOM_FQ_NAME = "kotlin.random.Random"
private const val KOTLIN_RANDOM_PACKAGE_NAME = "kotlin.random"
private const val KOTLIN_RANDOM_NAME = "Random"
