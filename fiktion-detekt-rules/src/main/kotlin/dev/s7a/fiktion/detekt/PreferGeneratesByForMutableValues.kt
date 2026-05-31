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
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports fixed mutable values that should be created by generator lambdas.
 */
public class PreferGeneratesByForMutableValues(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer generator lambdas for mutable Fiktion rule values.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()
    private val requiredImports = mutableSetOf<String>()
    private val mutableFactoryCalls: Set<String> by config(DEFAULT_MUTABLE_FACTORY_CALLS.toList()) { calls -> calls.toSet() }

    override fun preVisit(root: KtFile) {
        replacements.clear()
        requiredImports.clear()
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (!expression.usesInfixOperation(FiktionOperation.Generates)) return

        val left = expression.left ?: return
        val target = left.fixedValueGeneratesTargetText() ?: return
        val mutableFactory = expression.right?.mutableFactoryText(mutableFactoryCalls) ?: return
        val replacement = renderGeneratorRule(target = target, mutableFactory = mutableFactory)

        report(
            Finding(
                entity = Entity.from(expression.right ?: expression),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    startOffset = expression.textRange.startOffset,
                    endOffset = expression.textRange.endOffset,
                    text = replacement,
                )
            requiredImports += FIKTION_GENERATES_BY_FQ_NAME
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != FiktionOperation.Generates.text) return

        val qualified = expression.parent as? KtDotQualifiedExpression ?: return
        if (qualified.selectorExpression != expression) return

        val target = qualified.receiverExpression.fixedValueGeneratesTargetText() ?: return
        val mutableFactory =
            expression.valueArguments
                .singleOrNull()
                ?.getArgumentExpression()
                ?.mutableFactoryText(mutableFactoryCalls) ?: return
        val replacement = renderGeneratorRule(target = target, mutableFactory = mutableFactory)

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Use `$replacement` instead of `${qualified.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements +=
                TextReplacement(
                    startOffset = qualified.textRange.startOffset,
                    endOffset = qualified.textRange.endOffset,
                    text = replacement,
                )
            requiredImports += FIKTION_GENERATES_BY_FQ_NAME
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements, requiredImports)
        }
    }
}

private fun KtExpression.mutableFactoryText(mutableFactoryCalls: Set<String>): String? {
    val call = this as? KtCallExpression ?: return null
    return text.takeIf { GeneratorCallMatcher(mutableFactoryCalls).matches(call) }
}

private fun renderGeneratorRule(
    target: String,
    mutableFactory: String,
): String = "$target ${FiktionOperation.GeneratesBy.text} { $mutableFactory }"

private const val FIKTION_GENERATES_BY_FQ_NAME = "dev.s7a.fiktion.generatesBy"

private val DEFAULT_MUTABLE_FACTORY_CALLS = setOf("mutableListOf", "mutableSetOf", "mutableMapOf")
