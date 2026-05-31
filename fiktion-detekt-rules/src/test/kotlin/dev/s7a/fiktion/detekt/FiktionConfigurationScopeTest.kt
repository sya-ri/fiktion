package dev.s7a.fiktion.detekt

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class FiktionConfigurationScopeTest {
    @Test
    fun `configuration block rules ignore matching code outside Fiktion configuration scopes`() {
        val cases =
            listOf(
                RuleCase(
                    name = "AvoidMultipleConfigsForRuleTarget",
                    rule = { AvoidMultipleConfigsForRuleTarget(Config.empty) },
                    code =
                        """
                        fun helper() {
                            type<String>() using FiktionConfig.String.length(4)
                            type<String>() using FiktionConfig.String.length(8)
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "AvoidMultipleGeneratorsForRuleTarget",
                    rule = { AvoidMultipleGeneratorsForRuleTarget(Config.empty) },
                    code =
                        """
                        fun helper() {
                            type<Int>() generates 1
                            type<Int>() generates 2
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "AvoidRuleDeclarationsInLoops",
                    rule = { AvoidRuleDeclarationsInLoops(Config.empty) },
                    code =
                        """
                        fun helper(values: List<Int>) {
                            values.forEach { value ->
                                type<Int>() generates value
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "PreferContainerConfigureHelpers",
                    rule = { PreferContainerConfigureHelpers(Config.empty) },
                    code =
                        """
                        fun helper() {
                            typeFamily<CustomList<*>>() generatesBy {
                                CustomList(List(2) { index -> fakeElement(index) })
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "PreferGroupedRuleTarget",
                    rule = { PreferGroupedRuleTarget(Config.empty) },
                    code =
                        """
                        fun helper() {
                            type<String>() using FiktionConfig.String.length(8)
                            type<String>() generates "value"
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "PreferRuleTargetDeclarationOrder",
                    rule = { PreferRuleTargetDeclarationOrder(Config.empty) },
                    code =
                        """
                        fun helper() {
                            type<String>() {
                                this generates "value"
                                this using FiktionConfig.String.length(8)
                            }
                        }
                        """.trimIndent(),
                ),
            )

        cases.forEach { case ->
            assertEquals(0, case.rule().lint(case.code).size, case.name)
        }
    }

    @Test
    fun `configuration block rules report inside Fiktion instance and global configuration scopes`() {
        val cases =
            listOf(
                RuleCase(
                    name = "AvoidMultipleConfigsForRuleTarget",
                    rule = { AvoidMultipleConfigsForRuleTarget(Config.empty) },
                    code =
                        """
                        fun test() {
                            Fiktion {
                                type<String>() using FiktionConfig.String.length(4)
                                type<String>() using FiktionConfig.String.length(8)
                            }
                            Fiktion.configure {
                                type<Int>() using FiktionConfig.Int.range(0..10)
                                type<Int>() using FiktionConfig.Int.range(20..30)
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "AvoidMultipleGeneratorsForRuleTarget",
                    rule = { AvoidMultipleGeneratorsForRuleTarget(Config.empty) },
                    code =
                        """
                        fun test() {
                            Fiktion {
                                type<Int>() generates 1
                                type<Int>() generates 2
                            }
                            Fiktion.configure {
                                type<String>() generates "first"
                                type<String>() generates "second"
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "AvoidRuleDeclarationsInLoops",
                    rule = { AvoidRuleDeclarationsInLoops(Config.empty) },
                    code =
                        """
                        fun test(values: List<Int>) {
                            Fiktion {
                                values.forEach { value ->
                                    type<Int>() generates value
                                }
                            }
                            Fiktion.configure {
                                values.forEach { value ->
                                    type<String>() generates value.toString()
                                }
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "PreferContainerConfigureHelpers",
                    rule = { PreferContainerConfigureHelpers(Config.empty) },
                    code =
                        """
                        fun test() {
                            Fiktion {
                                typeFamily<CustomList<*>>() generatesBy {
                                    CustomList(List(2) { index -> fakeElement(index) })
                                }
                            }
                            Fiktion.configure {
                                typeFamily<CustomMap<*, *>>() generatesBy {
                                    CustomMap(List(2) { index -> fakeKey(index) to fakeValue(index) })
                                }
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "PreferGroupedRuleTarget",
                    rule = { PreferGroupedRuleTarget(Config.empty) },
                    code =
                        """
                        fun test() {
                            Fiktion {
                                type<String>() using FiktionConfig.String.length(8)
                                type<String>() generates "value"
                            }
                            Fiktion.configure {
                                type<Int>() using FiktionConfig.Int.range(0..10)
                                type<Int>() generates 1
                            }
                        }
                        """.trimIndent(),
                ),
                RuleCase(
                    name = "PreferRuleTargetDeclarationOrder",
                    rule = { PreferRuleTargetDeclarationOrder(Config.empty) },
                    code =
                        """
                        fun test() {
                            Fiktion {
                                type<String>() {
                                    this generates "value"
                                    this using FiktionConfig.String.length(8)
                                }
                            }
                            Fiktion.configure {
                                type<Int>() {
                                    this generates 1
                                    this using FiktionConfig.Int.range(0..10)
                                }
                            }
                        }
                        """.trimIndent(),
                ),
            )

        cases.forEach { case ->
            assertEquals(2, case.rule().lint(case.code).size, case.name)
        }
    }
}

private data class RuleCase(
    val name: String,
    val rule: () -> Rule,
    val code: String,
)
