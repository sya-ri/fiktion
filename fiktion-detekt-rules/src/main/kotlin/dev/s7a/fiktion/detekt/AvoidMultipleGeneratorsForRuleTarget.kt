package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBlockExpression

/**
 * Reports rule targets that register multiple generators in the same scope.
 */
public class AvoidMultipleGeneratorsForRuleTarget(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid multiple Fiktion generators for the same rule target.",
    ) {
    override fun visitBlockExpression(expression: KtBlockExpression) {
        super.visitBlockExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return

        expression
            .repeatedDeclarations(
                declaration = { statement -> statement.generatorRuleTargetText() },
                key = { target -> target.text },
            ).forEach { target ->
                report(
                    Finding(
                        entity = Entity.from(target.expression),
                        message = "Avoid multiple generators for `${target.text}` in the same scope.",
                    ),
                )
            }
    }
}
