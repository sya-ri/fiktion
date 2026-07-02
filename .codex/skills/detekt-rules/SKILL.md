---
name: detekt-rules
description: Repository contributor workflow for implementing, documenting, and testing custom detekt rules in the fiktion-detekt-rules module. Use when Codex needs to add, modify, refactor, or review a detekt rule, update the rule set, add autocorrect behavior, write rule tests, write Fiktion core before/after behavior tests, or update detekt rule documentation in this repository.
---

# Detekt Rules

Use this skill for work in `fiktion-detekt-rules`, especially new rule implementation, rule refactoring, autocorrect, rule ordering, docs, and tests.

## Source Files

- Rule implementations: `fiktion-detekt-rules/src/main/kotlin/dev/s7a/fiktion/detekt`
- Rule unit tests: `fiktion-detekt-rules/src/test/kotlin/dev/s7a/fiktion/detekt`
- Runtime before/after tests: `fiktion-core/src/commonTest/kotlin/dev/s7a/fiktion/detekt`
- Rule set registration: `fiktion-detekt-rules/src/main/kotlin/dev/s7a/fiktion/detekt/FiktionRuleSetProvider.kt`
- Shared PSI helpers: `fiktion-detekt-rules/src/main/kotlin/dev/s7a/fiktion/detekt/PsiExtensions.kt`
- Text autocorrect helpers: `fiktion-detekt-rules/src/main/kotlin/dev/s7a/fiktion/detekt/TextReplacement.kt`
- User-facing docs: `fiktion-detekt-rules/README.md`
- Project skill reference: `skills/fiktion/references/detekt-rules.md`

Trust source and tests over docs if they disagree, then update docs.

## Implementation Workflow

1. Read similar existing rules before coding.
   - Report-only rules: `AvoidMultipleGeneratorsForRuleTarget`, `AvoidInvalidTypeFamilyFakeIndex`, `ForbiddenGeneratorCall`.
   - Autocorrect rules: `PreferFixedConfigValue`, `PreferGroupedRuleTarget`, `PreferTypeRuleForNonGeneric`.
   - Generator-body rules: `AvoidRandomInstanceInGenerator`, `ForbiddenGeneratorCall`, `AvoidRecursiveFakeInGenerator`.
   - Type-family/container rules: `PreferContainerPartFakeHelpers`, `PreferIndexedFakeInTypeFamilyLoop`, `PreferContainerConfigureHelpers`.

2. Prove the runtime reason in `fiktion-core` when the rule recommends a different Fiktion DSL form.
   - Add a focused test under `fiktion-core/src/commonTest/kotlin/dev/s7a/fiktion/detekt/<RuleName>Test.kt`.
   - For “prefer X over Y” rules, show old and new forms are equivalent.
   - For “avoid invalid/dangerous X” rules, show the avoided pattern fails, leaks state, or has last-wins behavior.
   - Prefer existing generated/built-in shapes such as `List<String>`, `Map<String, Int>`, or existing `User` test fixtures over private local data classes unless metadata support is known.
   - Import DSL helpers explicitly only when required by extension resolution, for example `generatesBy`, `invoke`, `element`, `key`, or `value`. Do not import `dev.s7a.fiktion.using` for `this using ...` on a `FakeSpec` receiver.

3. Implement the rule in `fiktion-detekt-rules/src/main/.../detekt`.
   - Prefer PSI-only detection first. Avoid type resolution unless the rule is not useful without it.
   - Add shared PSI helpers to `PsiExtensions.kt` when more than one rule or test can reuse them.
   - For rules that inspect Fiktion configuration declarations, restrict detection to Fiktion configuration scopes.
     Use shared helpers such as `isInsideFiktionConfigurationScope()` and `isLambdaBodyOfCall(...)` instead of
     ad hoc parent traversal. Preserve support for both `Fiktion { ... }` and `Fiktion.configure { ... }`.
   - Keep rule classes focused on traversal and reporting; move parsing/rendering helpers out when they are generic.
   - Use report-only behavior unless autocorrect is obviously semantics-preserving.
   - For autocorrect, collect `TextReplacement`s and apply them in `postVisit`; use `KtFile.modifiedText` in tests.
   - Preserve or add needed `dev.s7a.fiktion.*` imports for autocorrect unless a matching star import already covers them.

4. Register and document the rule before final validation.
   - Add the rule constructor to `FiktionRuleSetProvider`.
   - Update `fiktion-detekt-rules/README.md`:
     - Add or adjust the rule table row with default state, autocorrect support, and one-line summary.
     - Add or adjust the explicit default `detekt.yml` entry, including all configurable options and defaults.
     - Add or adjust the `### <RuleName>` section with a consistent structure:
       - A short paragraph defining what the rule reports and why.
       - `#### Configuration options:` with either a table or `This rule has no configuration options.`
       - `#### Noncompliant Code:` with a focused Kotlin example.
       - `#### Compliant Code:` with the intended form.
       - `#### Limitations` for report-only rationale, PSI limits, skipped cases, or autocorrect/import notes.
   - Update `skills/fiktion/references/detekt-rules.md`:
     - Keep the short rule list aligned with README.
     - Keep the public reference user-facing; do not add repository implementation notes there.
   - Keep rules in alphabetical order everywhere rules are listed: `FiktionRuleSetProvider`, README table, README `detekt.yml`, README `###` sections, and `skills/fiktion/references/detekt-rules.md`.
   - Put `PreferGeneratesByForMutableValues`, `PreferGeneratesForFixedValue`, `PreferGeneratesInForRange`, `PreferGeneratesOneOf` in that exact alphabetical order.

5. Write detekt rule tests.
   - Add tests under `fiktion-detekt-rules/src/test/kotlin/dev/s7a/fiktion/detekt/<RuleName>Test.kt`.
   - Cover positive reports, non-reporting cases, and configuration options.
   - For rules that inspect configuration declarations, cover that matching code outside Fiktion configuration scopes is
     ignored, and that both `Fiktion { ... }` and `Fiktion.configure { ... }` still report.
   - For autocorrect, compile with `compileContentForTest`, enable `AUTO_CORRECT_KEY`, call `lint(ktFile)`, and assert `ktFile.modifiedText`.
   - For report-only rules, assert no `modifiedText`.
   - Prefer message assertions for representative findings instead of every finding if order is incidental.

## Common Patterns

Use existing helpers instead of ad hoc text checks where possible:

```kotlin
if (!expression.isInsideOperationArgument(FiktionOperation.GeneratesBy)) return
if (!expression.isInsideFiktionConfigurationScope()) return
if (!expression.isFiktionConfigCall()) return
val keyText = expression.fiktionConfigKeyText() ?: return
val target = expression.left?.fiktionRuleTargetText(FIKTION_GENERATOR_RULE_TARGET_CALLS) ?: return
```

Use `isInsideFiktionConfigurationScope()` for configuration-scope rules and `isLambdaBodyOfCall(...)` when a rule only
applies to a specific DSL lambda body, such as `fake { ... }`.

Use `GeneratorCallMatcher` for configurable call-name matching in generator lambdas.

Use `FiktionCall` and `FiktionOperation` enum entries instead of repeating DSL names as raw strings, unless the name is truly external configuration.

When matching configured loop-like calls, use `DEFAULT_LOOP_CALLS` or `DEFAULT_INDEXED_LOOP_CALLS` and expose config only if users need to extend the list.

## Test Commands

Run the narrow tests first:

```sh
export GRADLE_USER_HOME='./.gradle/user-home'
./gradlew --no-daemon :fiktion-detekt-rules:test
./gradlew --no-daemon :fiktion-core:jvmTest
```

Before finishing, run at least:

```sh
export GRADLE_USER_HOME='./.gradle/user-home'
./gradlew --no-daemon :fiktion-core:jvmTest :fiktion-detekt-rules:check
```

For final confidence after broad rule changes, run:

```sh
export GRADLE_USER_HOME='./.gradle/user-home'
./gradlew --no-daemon -Pkotlin.compiler.execution.strategy=in-process check
```

If Gradle fails with local socket or file-lock permission errors in the sandbox, rerun the same command with escalation.

## Review Checklist

- Rule name matches behavior and existing naming style.
- Report message says what to do, not only what is wrong.
- Configuration-scope rules ignore matching code outside Fiktion configuration scopes and support both `Fiktion { ... }`
  and `Fiktion.configure { ... }`.
- Rule is alphabetically ordered in provider, docs, and references.
- Runtime before/after test exists for DSL-preference rules.
- Rule tests cover positive, negative, config, and autocorrect paths as applicable.
- README table, default `detekt.yml`, and rule section are updated.
- `skills/fiktion/references/detekt-rules.md` is updated when the public rule list or authoring guidance changes.
- `:fiktion-core:jvmTest :fiktion-detekt-rules:check` passes.
