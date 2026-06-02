# Fiktion Detekt Rules

## Install

Use the optional detekt rules artifact when a project wants style feedback for Fiktion DSL usage:

```kotlin
dependencies {
    detektPlugins("dev.s7a:fiktion-detekt-rules:0.4.1")
}
```

The rules are provided under the `fiktion` rule set.

Autocorrect should keep corrected code compilable. When a correction introduces a Fiktion DSL symbol, add the matching
Fiktion import unless an existing direct or star import already covers it.

## Rules

- `AvoidContainerPartOnStarProjectedUnknown`: avoids `element`, `key`, or `value` targets on star-projected container targets. Report-only.
- `AvoidEmptyOneOf`: avoids empty `oneOf` and `generatesOneOf` value sets. Report-only.
- `AvoidGlobalConfigureInLocalTest`: avoids global `Fiktion.configure` calls inside local test functions. Report-only.
- `AvoidGlobalFiktionConfigureWithoutRestore`: avoids stored `Fiktion.configure` snapshots without restore calls. Report-only.
- `AvoidInvalidConfigRange`: avoids statically invalid Fiktion config ranges. Report-only.
- `AvoidInvalidTypeFamilyFakeIndex`: avoids invalid `fake(index)` calls in type-family generators. Report-only.
- `AvoidMultipleConfigsForRuleTarget`: avoids multiple configs for the same rule target and config key. Report-only.
- `AvoidMultipleGeneratorsForRuleTarget`: avoids multiple generators for the same rule target. Report-only.
- `AvoidMultipleSeedsInFakeSpec`: avoids multiple `withSeed` declarations in the same Fiktion spec block. Report-only.
- `AvoidNonPropertyRuleTargets`: avoids `type`, `typeFamily`, and `name` rule targets in local `fake { ... }` and scoped `Fiktion { ... }` rules when teams want explicit property-only rules. Report-only. Disabled by default.
- `AvoidRandomInstanceInGenerator`: avoids `Random` instances inside generator lambdas. Supports autocorrect.
- `AvoidRecursiveFakeInGenerator`: avoids recursive fake calls in same-type generators. Report-only.
- `AvoidRuleDeclarationsInLoops`: avoids declaring rules inside loops. Report-only.
- `AvoidUnusedFiktionSnapshot`: avoids discarding `Fiktion.configure` snapshots. Report-only.
- `ForbiddenGeneratorCall`: avoids configured calls such as assertions, current-time APIs, random APIs, file reads, and process APIs inside generators. Report-only.
- `PreferContainerConfigureHelpers`: prefers `configureCollection` or `configureMap` for container materialization rules. Report-only.
- `PreferContainerPartFakeHelpers`: prefers `fakeElement`, `fakeKey`, and `fakeValue` in type-family generators. Supports autocorrect.
- `PreferExplicitFakeSeedName`: prefers explicit `seed = ...` names for reified `fake<T>` calls. Supports autocorrect.
- `PreferFixedConfigValue`: prefers fixed config values over equal-bound config ranges. Supports autocorrect.
- `PreferFixedDefaultProbability`: prefers fixed generated values over `orDefaultAt 0.0`, `orDefaultAt 1.0`, `orDefaultAt 0.percent`, and `orDefaultAt 100.percent`. Supports autocorrect.
- `PreferFixedNullProbability`: prefers fixed generated values over `orNullAt 0.0`, `orNullAt 1.0`, `orNullAt 0.percent`, and `orNullAt 100.percent`. Supports autocorrect.
- `PreferGeneratesByForMutableValues`: prefers generator lambdas for mutable values. Supports autocorrect.
- `PreferGeneratesForFixedValue`: prefers fixed values over generators that always produce a fixed value. Supports autocorrect.
- `PreferGeneratesInForRange`: prefers `generatesIn` for range generators. Supports autocorrect.
- `PreferGeneratesOneOf`: prefers `generatesOneOf` for multi-argument `oneOf` generators. Supports autocorrect.
- `PreferGroupedRuleTarget`: prefers grouped blocks for repeated rule targets. Supports autocorrect.
- `PreferIndexedFakeInTypeFamilyLoop`: prefers loop-indexed fake calls in type-family loops. Supports autocorrect.
- `PreferInfixFiktionDsl`: prefers infix Fiktion DSL declarations over dot-call syntax. Supports autocorrect.
- `PreferNamedTypeFamilyFakeArgumentIndex`: prefers named `argumentIndex = ...` arguments in type-family fake helper calls. Supports autocorrect.
- `PreferPropertyShorthand`: prefers `User::property` shorthand over `property(User::property)` targets. Supports autocorrect.
- `PreferRuleTargetDeclarationOrder`: prefers configs before generators in grouped target blocks. Supports autocorrect.
- `PreferSeedParameter`: prefers `fake(seed = ...)` over `withSeed` inside a `fake` block. Report-only.
- `PreferThisUsingInFakeSpec`: prefers `this using ...` over bare `using(...)` calls. Supports autocorrect.
- `PreferTypeRuleForNonGeneric`: prefers exact type rules over non-generic type-family rules. Supports autocorrect.

## Authoring Rules

Before adding a rule that recommends another Fiktion DSL form, add a focused Fiktion API test proving either the
original and recommended forms are equivalent or the recommended form preserves the intended behavior better. Keep those
tests in `fiktion-core/src/commonTest/kotlin/dev/s7a/fiktion/detekt`, split by rule name.

Keep individual rule docs short in user-facing docs. README should list the rule and intent; detailed examples should
live in tests or dedicated rule docs if they become necessary.

When adding or changing a rule, update `fiktion-detekt-rules/README.md` in the same change:

- Add or adjust the rule table row.
- Add or adjust the explicit default `detekt.yml` entry, including configurable defaults.
- Add or adjust the `### <RuleName>` section with intent, examples or limitations when useful, and autocorrect import notes when relevant.

Keep the rule list, README table, README default config, and README rule sections in alphabetical order.

Prefer PSI-only rules first. Add import alias support when it is cheap. Avoid type resolution until a rule cannot be
useful without it, because it increases setup and runtime cost for users.

Rules that inspect Fiktion configuration declarations should ignore matching code outside Fiktion configuration scopes.
Use shared PSI helpers such as `isInsideFiktionConfigurationScope()` and `isLambdaBodyOfCall(...)` instead of local
parent traversal. Add tests proving non-Fiktion code is ignored and both `Fiktion { ... }` and `Fiktion.configure { ... }`
still report.

For autocorrect in detekt 2 tests, prefer `KtFile.modifiedText` over direct PSI `replace` when the standalone test
environment lacks IntelliJ extension points required by tree replacement.

Autocorrect import handling currently matters for:

- `PreferFixedDefaultProbability`: may introduce `dev.s7a.fiktion.default` and `dev.s7a.fiktion.generates`.
- `PreferFixedNullProbability`: may introduce `dev.s7a.fiktion.generates`.
- `PreferGeneratesByForMutableValues`: may introduce `dev.s7a.fiktion.generatesBy`.
- `PreferGeneratesForFixedValue`: may introduce `dev.s7a.fiktion.generates`.
- `PreferGeneratesInForRange`: may introduce `dev.s7a.fiktion.generatesIn`.
- `PreferGeneratesOneOf`: may introduce `dev.s7a.fiktion.generatesOneOf`.
- `PreferGroupedRuleTarget`: may introduce `dev.s7a.fiktion.invoke`.
- `PreferTypeRuleForNonGeneric`: may introduce `dev.s7a.fiktion.type`.
