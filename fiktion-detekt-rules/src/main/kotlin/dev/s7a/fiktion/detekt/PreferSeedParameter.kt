package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Reports fake calls that can pass their seed as a named call parameter.
 */
public class PreferSeedParameter(
    config: Config,
) : Rule(
        config = config,
        description = "Prefer fake(seed = ...) over withSeed inside a fake block.",
    ) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != FiktionCall.Fake.text) return
        if (expression.hasArgumentNamed(FiktionParameter.Seed)) return

        val lambda = expression.lambdaArguments.singleOrNull()?.getLambdaExpression() ?: return
        val seedStatement =
            lambda.bodyExpression
                ?.statements
                ?.singleOrNull { statement -> statement.isWithSeedStatement() } ?: return
        val seedText = seedStatement.withSeedArgumentText() ?: return

        report(
            Finding(
                entity = Entity.from(seedStatement),
                message = "Pass this seed as `fake(seed = $seedText)` instead of declaring `withSeed` inside the fake block.",
            ),
        )
    }
}
