package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Reports raw type-family fake calls where a container-part helper should be used.
 */
public class PreferContainerPartFakeHelpers(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer container-part fake helpers in type-family generators.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private val indexedLoopCalls: Set<String> by config(DEFAULT_INDEXED_LOOP_CALLS.toList()) { calls -> calls.toSet() }

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.hasCallee(FiktionCall.Fake)) return
        if (!expression.isInsideTypeFamilyGeneratesBy()) return

        val arguments = expression.fakeArguments() ?: return
        val helper = expression.containerPartFakeHelper(arguments.argumentIndex, indexedLoopCalls) ?: return
        val replacement = "${helper.text}(${arguments.indexText})"

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${expression.text}` so container target config applies.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    startOffset = expression.textRange.startOffset,
                    endOffset = expression.textRange.endOffset,
                    text = replacement,
                )
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }
}

private data class ContainerFakeArguments(
    val indexText: String,
    val argumentIndex: Int,
)

private fun KtCallExpression.fakeArguments(): ContainerFakeArguments? {
    val indexArgument = valueArguments.indexArgumentExpression() ?: return null
    val argumentIndex = valueArguments.argumentIndexValue() ?: return null
    return ContainerFakeArguments(indexText = indexArgument.text, argumentIndex = argumentIndex)
}

private fun List<KtValueArgument>.indexArgumentExpression(): KtExpression? {
    val namedIndex = firstOrNull { argument -> argument.getArgumentName()?.asName?.identifier == "index" }?.getArgumentExpression()
    if (namedIndex != null) return namedIndex
    return firstOrNull { argument -> argument.getArgumentName() == null }?.getArgumentExpression()
}

private fun List<KtValueArgument>.argumentIndexValue(): Int? {
    val argument =
        firstOrNull { argument -> argument.getArgumentName()?.asName?.identifier == "argumentIndex" }?.getArgumentExpression()
            as? KtConstantExpression ?: return null
    val text = argument.text.takeIf { value -> value.all(Char::isDigit) } ?: return null
    return text.toIntOrNull()
}

private fun KtCallExpression.containerPartFakeHelper(
    argumentIndex: Int,
    indexedLoopCalls: Set<String>,
): FiktionCall? =
    mapPartFakeHelper(argumentIndex)
        ?: collectionPartFakeHelper(argumentIndex, indexedLoopCalls).takeUnless { isInsideToExpression() }

private fun KtCallExpression.collectionPartFakeHelper(
    argumentIndex: Int,
    indexedLoopCalls: Set<String>,
): FiktionCall? {
    if (argumentIndex != 0) return null
    if (containingIndexedLoopCall(indexedLoopCalls) == null) return null
    return FiktionCall.FakeElement
}

private fun KtCallExpression.mapPartFakeHelper(argumentIndex: Int): FiktionCall? {
    val pairExpression = containingToExpression() ?: return null

    return when {
        pairExpression.left?.contains(this) == true && argumentIndex == 0 -> FiktionCall.FakeKey
        pairExpression.right?.contains(this) == true && argumentIndex == 1 -> FiktionCall.FakeValue
        else -> null
    }
}

private fun KtCallExpression.isInsideToExpression(): Boolean = containingToExpression() != null

private fun KtCallExpression.containingToExpression(): KtBinaryExpression? =
    generateSequence(parent) { element -> element.parent }
        .filterIsInstance<KtBinaryExpression>()
        .firstOrNull { expression -> expression.operationReference.text == "to" && expression.contains(this) }

private fun KtCallExpression.containingIndexedLoopCall(indexedLoopCalls: Set<String>): KtCallExpression? =
    generateSequence(getStrictParentOfType<KtLambdaExpression>()) { lambda ->
        lambda.getStrictParentOfType<KtLambdaExpression>()
    }.firstNotNullOfOrNull { lambda ->
        lambda.containingCallExpression()?.takeIf { lambda.isIndexedLoopLambda(indexedLoopCalls) && lambda.contains(this) }
    }
