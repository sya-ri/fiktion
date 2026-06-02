package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBlockExpression

/**
 * Reports repeated seed declarations in the same Fiktion spec block.
 */
public class AvoidMultipleSeedsInFakeSpec(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid multiple withSeed declarations in the same Fiktion spec block.",
    ) {
    override fun visitBlockExpression(expression: KtBlockExpression) {
        super.visitBlockExpression(expression)

        if (!expression.isLambdaBodyOfCall(FiktionCall.Fake)) return

        val seedStatements = expression.statements.filter { statement -> statement.isWithSeedStatement() }
        seedStatements.drop(1).forEach { statement ->
            report(
                Finding(
                    entity = Entity.from(statement),
                    message = "Use only one `withSeed` declaration in the same Fiktion spec block.",
                ),
            )
        }
    }
}
