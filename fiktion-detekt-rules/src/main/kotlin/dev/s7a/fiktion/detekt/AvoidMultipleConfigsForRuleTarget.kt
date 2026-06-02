package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBlockExpression

/**
 * Reports rule targets that configure the same config key multiple times in the same scope.
 */
public class AvoidMultipleConfigsForRuleTarget(
    config: Config,
) : Rule(
        config = config,
        description = "Avoid multiple Fiktion config values for the same rule target and config key.",
    ) {
    override fun visitBlockExpression(expression: KtBlockExpression) {
        super.visitBlockExpression(expression)

        if (!expression.isInsideFiktionConfigurationScope()) return

        expression
            .repeatedDeclarations(
                declaration = { statement -> statement.ruleTargetConfigText() },
                key = { config -> TargetConfigKey(targetText = config.targetText, configKeyText = config.configKeyText) },
            ).forEach { config ->
                report(
                    Finding(
                        entity = Entity.from(config.expression),
                        message = "Avoid multiple configs for `${config.targetText}` with `${config.configKeyText}` in the same scope.",
                    ),
                )
            }
    }
}

private data class TargetConfigKey(
    val targetText: String,
    val configKeyText: String,
)
