package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports property(...) rule targets that can use the KProperty shorthand.
 */
public class PreferPropertyShorthand(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer KProperty shorthand Fiktion rule targets.",
    ) {
    private val replacements = mutableListOf<TextReplacement>()

    override fun preVisit(root: KtFile) {
        replacements.clear()
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        val operation = PROPERTY_SHORTHAND_OPERATIONS.firstOrNull { operation -> expression.usesInfixOperation(operation) } ?: return
        val propertyCall = expression.left as? KtCallExpression ?: return
        if (!propertyCall.hasCallee(FiktionCall.Property)) return
        val reference =
            propertyCall.valueArguments
                .singleOrNull()
                ?.getArgumentExpression() as? KtCallableReferenceExpression ?: return
        val right = expression.right ?: return
        val replacement = "${reference.text} ${operation.text} ${right.text}"

        report(
            Finding(
                entity = Entity.from(propertyCall),
                message = "Use `$replacement` instead of `${expression.text}`.",
            ),
        )
        if (autoCorrect) {
            replacements += TextReplacement(expression.textRange.startOffset, expression.textRange.endOffset, replacement)
        }
    }

    override fun postVisit(root: KtFile) {
        if (autoCorrect) {
            root.applyReplacements(replacements)
        }
    }
}

private val PROPERTY_SHORTHAND_OPERATIONS = FIKTION_GENERATOR_OPERATIONS + FiktionOperation.Using
