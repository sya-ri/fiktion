# Fiktion Detekt Rules

`fiktion-detekt-rules` provides optional detekt rules for keeping Fiktion DSL usage focused and consistent.

## Install

First configure detekt itself by following the
[official detekt Gradle setup](https://detekt.dev/docs/gettingstarted/gradle/). Then add the Fiktion rules artifact as a
detekt plugin dependency:

```kotlin
dependencies {
    detektPlugins("dev.s7a:fiktion-detekt-rules:0.5.1")
}
```

The rules are provided under the `fiktion` rule set.

Auto-correcting rules preserve existing imports where possible. When a correction introduces a Fiktion DSL symbol that
is not already imported, the rule adds the needed `dev.s7a.fiktion.*` member import unless a matching star import already
covers it.

## Fiktion Rule Set

Rule Set ID: `fiktion`

Checked cells mean the rule is enabled by default or supports auto-correct.

| Rule | Enabled | Auto-correct | Summary |
| ---- | ------- | ------------ | ------- |
| [AvoidContainerPartOnStarProjectedUnknown](#avoidcontainerpartonstarprojectedunknown) | ✅ |  | Avoid element, key, or value targets on star-projected container targets. |
| [AvoidEmptyOneOf](#avoidemptyoneof) | ✅ |  | Avoid empty oneOf value sets. |
| [AvoidGlobalConfigureInLocalTest](#avoidglobalconfigureinlocaltest) | ✅ |  | Avoid global Fiktion.configure calls inside local test functions. |
| [AvoidGlobalFiktionConfigureWithoutRestore](#avoidglobalfiktionconfigurewithoutrestore) | ✅ |  | Avoid stored Fiktion.configure snapshots without restore calls. |
| [AvoidInvalidConfigRange](#avoidinvalidconfigrange) | ✅ |  | Avoid statically invalid Fiktion config ranges. |
| [AvoidInvalidTypeFamilyFakeIndex](#avoidinvalidtypefamilyfakeindex) | ✅ |  | Avoid invalid fake indexes in type-family generators. |
| [AvoidMultipleConfigsForRuleTarget](#avoidmultipleconfigsforruletarget) | ✅ |  | Avoid multiple configs for the same rule target and config key. |
| [AvoidMultipleGeneratorsForRuleTarget](#avoidmultiplegeneratorsforruletarget) | ✅ |  | Avoid multiple generators for the same rule target. |
| [AvoidMultipleSeedsInFakeSpec](#avoidmultipleseedsinfakespec) | ✅ |  | Avoid multiple withSeed declarations in the same Fiktion spec block. |
| [AvoidNonPropertyRuleTargets](#avoidnonpropertyruletargets) |  |  | Avoid non-property rule targets in `fake { ... }` and `Fiktion { ... }`. |
| [AvoidRandomInstanceInGenerator](#avoidrandominstanceingenerator) | ✅ | ✅ | Avoid Random instances inside generator lambdas. |
| [AvoidRecursiveFakeInGenerator](#avoidrecursivefakeingenerator) | ✅ |  | Avoid recursive fake calls in same-type generators. |
| [AvoidRuleDeclarationsInLoops](#avoidruledeclarationsinloops) | ✅ |  | Avoid declaring rules inside loops. |
| [AvoidUnusedFiktionSnapshot](#avoidunusedfiktionsnapshot) | ✅ |  | Avoid discarding Fiktion.configure snapshots. |
| [ForbiddenGeneratorCall](#forbiddengeneratorcall) | ✅ |  | Avoid configured calls inside generator lambdas. |
| [PreferContainerConfigureHelpers](#prefercontainerconfigurehelpers) | ✅ |  | Prefer configureCollection or configureMap for container materialization rules. |
| [PreferContainerPartFakeHelpers](#prefercontainerpartfakehelpers) | ✅ | ✅ | Prefer container-part helpers in type-family generators. |
| [PreferExplicitFakeSeedName](#preferexplicitfakeseedname) | ✅ | ✅ | Prefer explicit seed names for fake calls. |
| [PreferFixedConfigValue](#preferfixedconfigvalue) | ✅ | ✅ | Prefer fixed config values over equal-bound config ranges. |
| [PreferFixedDefaultProbability](#preferfixeddefaultprobability) | ✅ | ✅ | Prefer fixed generated values over zero or full default probabilities. |
| [PreferFixedNullProbability](#preferfixednullprobability) | ✅ | ✅ | Prefer fixed generated values over zero or full null probabilities. |
| [PreferGeneratesByForMutableValues](#prefergeneratesbyformutablevalues) | ✅ | ✅ | Prefer generator lambdas for mutable values. |
| [PreferGeneratesForFixedValue](#prefergeneratesforfixedvalue) | ✅ | ✅ | Prefer fixed values over generators that always produce a fixed value. |
| [PreferGeneratesInForRange](#prefergeneratesinforrange) | ✅ | ✅ | Prefer generatesIn for range generators. |
| [PreferGeneratesOneOf](#prefergeneratesoneof) | ✅ | ✅ | Prefer generatesOneOf for oneOf generators. |
| [PreferGroupedRuleTarget](#prefergroupedruletarget) | ✅ | ✅ | Prefer grouped blocks for repeated rule targets. |
| [PreferIndexedFakeInTypeFamilyLoop](#preferindexedfakeintypefamilyloop) | ✅ | ✅ | Prefer indexed fake calls in type-family loops. |
| [PreferInfixFiktionDsl](#preferinfixfiktiondsl) | ✅ | ✅ | Prefer infix Fiktion DSL declarations over dot-call syntax. |
| [PreferNamedTypeFamilyFakeArgumentIndex](#prefernamedtypefamilyfakeargumentindex) | ✅ | ✅ | Prefer named argumentIndex in type-family fake helper calls. |
| [PreferPropertyShorthand](#preferpropertyshorthand) | ✅ | ✅ | Prefer KProperty shorthand rule targets. |
| [PreferRuleTargetDeclarationOrder](#preferruletargetdeclarationorder) | ✅ | ✅ | Prefer configs before generators in grouped target blocks. |
| [PreferSeedParameter](#preferseedparameter) | ✅ |  | Prefer fake(seed = ...) over withSeed inside a fake block. |
| [PreferThisUsingInFakeSpec](#preferthisusinginfakespec) | ✅ | ✅ | Prefer this using ... over bare using(...) calls. |
| [PreferTypeRuleForNonGeneric](#prefertyperulefornongeneric) | ✅ | ✅ | Prefer exact type rules over non-generic type-family rules. |

### AvoidContainerPartOnStarProjectedUnknown

This rule reports `element`, `key`, and `value` targets selected from star-projected container targets such as
`type<List<*>>().element`. Use concrete type arguments so the target type is explicit.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<List<*>>().element generates "value"
type<Map<*, *>>().key generates "key"
type<Map<*, *>>().value generates 1
```

#### Compliant Code:

```kotlin
type<List<String>>().element generates "value"
type<Map<String, Int>>().key generates "key"
type<Map<String, Int>>().value generates 1
```

#### Limitations

The rule is report-only because the correct concrete type arguments depend on the intended generated container type.
It checks direct rule targets and does not expand type aliases.

### AvoidEmptyOneOf

This rule reports `oneOf()` and empty `generatesOneOf` value sets because generation cannot select a value.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<String>() generatesBy {
    oneOf()
}

type<String>() generatesOneOf emptyList()
```

#### Compliant Code:

```kotlin
type<String>() generatesBy {
    oneOf("a", "b")
}

type<String>() generatesOneOf listOf("a", "b")
```

#### Limitations

The rule is report-only because it cannot infer the correct fallback value. It reports statically empty value sets.

### AvoidGlobalConfigureInLocalTest

This rule reports `Fiktion.configure { ... }` inside local test functions. Prefer per-call `fake { ... }` configuration
or a scoped `Fiktion { ... }` instance when rules do not intentionally need to affect global top-level `fake<T>()`
calls.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `testAnnotations` | `Test`, `kotlin.test.Test`, `org.junit.Test` | Annotation names treated as local test functions. |
| `testNamePrefixes` | `test` | Function name prefixes treated as local test functions. |

#### Noncompliant Code:

```kotlin
@Test
fun `generates user`() {
    Fiktion.configure {
        type<String>() generates "id"
    }
}
```

#### Compliant Code:

```kotlin
@Test
fun `generates user`() {
    val user =
        fake<User> {
            User::id generates "id"
        }
}

@Test
fun `generates user with scoped fiktion`() {
    val fiktion =
        Fiktion {
            type<String>() generates "id"
        }
    val user = fiktion.fake<User>()
}
```

#### Limitations

The rule is report-only because migrating global configuration to per-call or scoped configuration depends on test
intent. It only reports calls inside functions recognized by annotation or configured name prefix.

### AvoidGlobalFiktionConfigureWithoutRestore

This rule reports stored `Fiktion.configure { ... }` snapshots that are not restored later in the same block. Restore
global configuration in `finally` when the configured state is only needed for a local scope.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fun test() {
    val snapshot =
        Fiktion.configure {
            type<String>() generates "local"
        }

    fake<String>()
}
```

#### Compliant Code:

```kotlin
fun test() {
    val snapshot =
        Fiktion.configure {
            type<String>() generates "local"
        }
    try {
        fake<String>()
    } finally {
        snapshot.restore(force = true)
    }
}
```

#### Limitations

The rule is report-only because it cannot know the correct lifetime for a global configuration. It checks direct
`val snapshot = Fiktion.configure { ... }` declarations and looks for a later `snapshot.restore(...)` call in the same
block.

### AvoidInvalidConfigRange

This rule reports statically invalid Fiktion config values and ranges, such as empty ranges for config keys and negative
values for size or length config.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
this using FiktionConfig.String.length(10..1)
this using FiktionConfig.Collection.size(-1)
```

#### Compliant Code:

```kotlin
this using FiktionConfig.String.length(1..10)
this using FiktionConfig.Collection.size(1)
```

#### Limitations

The rule is report-only because the intended valid range depends on the generated value. It checks statically visible
numeric literals and ranges, and intentionally skips values stored in variables.

### AvoidInvalidTypeFamilyFakeIndex

This rule reports `fake(index)` calls in type-family generators when `index` is outside the requested type arguments.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
typeFamily<Box<*>>() generatesBy {
    Box(fake(1))
}
```

#### Compliant Code:

```kotlin
typeFamily<Box<*>>() generatesBy {
    Box(fake(0))
}
```

#### Limitations

The rule is report-only because the correct argument index depends on generator intent. It checks direct generic
`typeFamily` targets such as `Box<*>` and `Pair<*, *>`, but intentionally skips type aliases because PSI-only analysis
cannot expand alias targets.

### AvoidMultipleConfigsForRuleTarget

This rule reports rule targets that configure the same config key multiple times in the same scope because the later
config replaces the earlier one.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<String>() using FiktionConfig.String.length(4)
type<String>() using FiktionConfig.String.length(8)

type<String>() {
    this using FiktionConfig.String.length(4)
    this using FiktionConfig.String.length(8)
}
```

#### Compliant Code:

```kotlin
type<String>() using FiktionConfig.String.length(8)

type<String>() {
    this using FiktionConfig.String.length(8)
}
```

#### Limitations

The rule is report-only because deciding which config value to keep depends on test intent. It checks direct config calls
such as `FiktionConfig.String.length(8)` and import aliases such as `Config.String.length(8)`, but intentionally skips
config settings stored in variables.

### AvoidMultipleGeneratorsForRuleTarget

This rule reports rule targets that register multiple generators in the same scope because the later generator replaces
the earlier one.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<Int>() generates 1
type<Int>() generatesBy { 2 }

type<String>() {
    this generates "first"
    this generatesBy { "second" }
}
```

#### Compliant Code:

```kotlin
type<Int>() generatesBy { 2 }

type<String>() {
    this generatesBy { "second" }
}
```

#### Limitations

The rule is report-only because deciding which generator to keep depends on test intent. It checks repeated generator
declarations in the same block and grouped target block, including `generates`, `generatesBy`, `generatesIn`, and
`generatesOneOf`.

### AvoidMultipleSeedsInFakeSpec

This rule reports multiple `withSeed` declarations in the same Fiktion spec block because the later seed replaces the
earlier one.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fake<User> {
    withSeed(123)
    this withSeed 456
}
```

#### Compliant Code:

```kotlin
fake<User>(seed = 456)
```

#### Limitations

The rule is report-only because it cannot know which seed declaration should be kept. Use `PreferSeedParameter` when a
single seed declaration can be moved to the `fake(seed = ...)` parameter.

### AvoidNonPropertyRuleTargets

This opt-in rule reports rule declarations in `fake { ... }` and `Fiktion { ... }` whose target is not an explicit
property target. It is useful for teams that want local fake configuration to document the property being controlled
instead of applying broad type, type-family, or property-name rules.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fake<User> {
    type<String>() generates "value"
    typeFamily<List<*>>() generatesBy { fake(0) }
    name<String>("id") generates "value"
}
```

#### Compliant Code:

```kotlin
fake<User> {
    User::id generates "value"
    property<User, String>("id") generates "value"
    (User::profile / Profile::nickname) generates "value"
}

Fiktion.configure {
    type<String>() generates "value"
}
```

#### Limitations

This rule is disabled by default because it constrains public Fiktion APIs that are valid and useful in many projects.
It only reports local `fake { ... }` and scoped `Fiktion { ... }` declarations; global configuration and addon-style
configuration remain valid places for broad type, type-family, or name rules. The rule is report-only because rewriting
broad type, type-family, or name rules to property rules requires project-specific intent.

### AvoidRandomInstanceInGenerator

This rule reports `kotlin.random.Random`, `Random.Default`, and `Random(...)` inside generator lambdas. Use the
generator's seed-derived `random` instead.

Auto-correct replaces the random instance expression with `random`, including receiver positions such as
`Random.Default.nextInt()`.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<Int>() generatesBy {
    Random.nextInt()
}

type<Int>() generatesBy {
    Random.Default.nextInt()
}
```

#### Compliant Code:

```kotlin
type<Int>() generatesBy {
    random.nextInt()
}
```

#### Limitations

The rule only targets random instance expressions that can be replaced with the generator context `random`. Other
non-deterministic calls are handled by `ForbiddenGeneratorCall`.

### AvoidRecursiveFakeInGenerator

This rule reports same-type `fake<T>()` calls inside `type<T>()` generator lambdas where generation can recurse forever.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<User>() generatesBy {
    fake<User>()
}
```

#### Compliant Code:

```kotlin
type<User>() generatesBy {
    if (depth > 2) User(children = emptyList()) else User(children = listOf(fake<ChildUser>()))
}
```

#### Limitations

The rule is report-only because the correct stopping condition depends on the domain model. It compares direct type text
and does not expand type aliases.

### AvoidRuleDeclarationsInLoops

This rule reports Fiktion rule declarations inside loops because registration count and iteration order become part of
the resulting config.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `loopCalls` | Kotlin indexed collection builders, `repeat`, and common collection traversal calls | Call names whose lambda is treated as a loop body. |

#### Noncompliant Code:

```kotlin
repeat(3) { index ->
    type<Int>() generates index
}

values.forEach { value ->
    type<String>() using FiktionConfig.String.length(value)
}
```

#### Compliant Code:

```kotlin
type<Int>() generatesBy {
    index
}

type<String>() using FiktionConfig.String.length(8)
```

#### Limitations

The rule is report-only because the replacement depends on whether the loop was intended to register one final rule or
to generate different values per fake value. It checks language loops and configured loop-like calls.

### AvoidUnusedFiktionSnapshot

This rule reports discarded `Fiktion.configure { ... }` calls. Store the returned snapshot and restore it after the test
scope to avoid leaking global configuration.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fun test() {
    Fiktion.configure {
        type<String>() generates "local"
    }
}
```

#### Compliant Code:

```kotlin
fun test() {
    val snapshot =
        Fiktion.configure {
            type<String>() generates "local"
        }
    try {
        fake<String>()
    } finally {
        snapshot.restore(force = true)
    }
}
```

#### Limitations

The rule is report-only because it cannot insert the correct restore scope. It reports discarded configure call
statements; stored snapshots without restore calls are handled by `AvoidGlobalFiktionConfigureWithoutRestore`.

### ForbiddenGeneratorCall

This rule reports configured calls inside generator lambdas. By default, it reports test assertions, current-time APIs,
random APIs, blocking calls, file reads, and process APIs because generators should be deterministic, fast, and
side-effect-free.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `calls` | Common assertions, current-time APIs, random APIs, blocking calls, file reads, and process APIs | Call names, infix operation names, or fully qualified call names to report inside generator lambdas. |

#### Noncompliant Code:

```kotlin
type<User>() generatesBy {
    val user = User(id = string())
    assertTrue(user.id.isNotBlank())
    user.id shouldBe "id"
    user
}

type<Instant>() generatesBy {
    Clock.System.now()
}

type<Int>() generatesBy {
    Random.nextInt()
}

type<String>() generatesBy {
    File("fixture.txt").readText()
}
```

#### Compliant Code:

```kotlin
type<Instant>() generates fixedInstant
type<Instant>() using FiktionConfig.Instant.epochSeconds(1_700_000_000L)
type<String>() generates fixture
```

#### Limitations

The rule is report-only because the correct replacement depends on the call. Move assertions to the test body. Replace
current-time calls, random APIs, and side-effectful calls with fixed values, seed-derived values, typed config, or test
setup outside the generator. Simple names such as `assertTrue` match both qualified and unqualified calls. Infix
operation entries such as `shouldBe` match infix expressions. Fully qualified entries such as `Clock.System.now` match
dot-qualified call text.

### PreferContainerConfigureHelpers

This rule reports `typeFamily<...>() generatesBy` container generators that materialize values from `fakeElement`,
`fakeKey`, or `fakeValue`. Prefer `configureCollection` or `configureMap` for custom container materialization.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `containerMaterializationCalls` | Kotlin collection builders and collection conversion calls | Call names allowed in a simple container materialization expression alongside `fakeElement`, `fakeKey`, and `fakeValue`. |

#### Noncompliant Code:

```kotlin
typeFamily<CustomList<*>>() generatesBy {
    CustomList(List(size) { index -> fakeElement(index) })
}

typeFamily<CustomMap<*, *>>() generatesBy {
    CustomMap(List(size) { index -> fakeKey(index) to fakeValue(index) }.toMap())
}
```

#### Compliant Code:

```kotlin
configureCollection<CustomList<*>> { elements ->
    CustomList(elements)
}

configureMap<CustomMap<*, *>> { entries ->
    CustomMap(entries.toMap())
}
```

#### Limitations

The rule is report-only because constructor names and conversion code depend on the custom container type. It reports
simple materialization generators that only assemble generated container parts. Add project-specific helper calls to
`containerMaterializationCalls` when a custom factory is part of simple materialization.

### PreferContainerPartFakeHelpers

This rule reports raw type-family `fake(index, argumentIndex = ...)` calls where `fakeElement`, `fakeKey`, or
`fakeValue` should be used so container target config applies.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `indexedLoopCalls` | Kotlin indexed collection builders and `repeat` | Call names whose lambda parameter is treated as a generated element index. |

#### Noncompliant Code:

```kotlin
typeFamily<CustomMap<*, *>>() generatesBy {
    CustomMap(List(size) { index -> fake(index, argumentIndex = 0) to fake(index, argumentIndex = 1) }.toMap())
}
```

#### Compliant Code:

```kotlin
typeFamily<CustomMap<*, *>>() generatesBy {
    CustomMap(List(size) { index -> fakeKey(index) to fakeValue(index) }.toMap())
}
```

#### Limitations

The rule only autocorrects explicit `argumentIndex` calls inside type-family generators. It rewrites `to` pair left and
right sides to `fakeKey` and `fakeValue`, and non-map indexed loop calls for argument `0` to `fakeElement`.

### PreferExplicitFakeSeedName

This rule reports `fake<T>(seed)` calls that pass the seed positionally. Use `seed = ...` so the numeric argument is
not confused with a generated value or type-family index.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fake<User>(123)
fiktion.fake<User>(456) {
    User::id generates "user-id"
}
```

#### Compliant Code:

```kotlin
fake<User>(seed = 123)
fiktion.fake<User>(seed = 456) {
    User::id generates "user-id"
}
```

#### Limitations

The rule autocorrects reified `fake<T>(...)` calls with a positional first value argument. It intentionally skips
low-level overloads such as `fake(type, 123)` because the first positional argument is not the seed.

### PreferFixedConfigValue

This rule reports Fiktion config calls that pass an equal-bound range where a fixed config value is clearer.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
this using FiktionConfig.Collection.size(2..2)
element using FiktionConfig.Int.range(42..42)
```

#### Compliant Code:

```kotlin
this using FiktionConfig.Collection.size(2)
element using FiktionConfig.Int.range(42)
```

#### Limitations

The rule checks direct `FiktionConfig` calls and aliases such as `import dev.s7a.fiktion.FiktionConfig as Config`.
It intentionally does not chase values stored in variables:

```kotlin
val size = 2..2
this using FiktionConfig.Collection.size(size)
```

### PreferFixedDefaultProbability

This rule reports `generates ... orDefaultAt ...` declarations whose default probability is fixed. Use the generated
value directly for `0.0` or `0.percent`, and use `generates default` for `1.0` or `100.percent`.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
User::profile generates Profile(nickname = "generated") orDefaultAt 0.0
User::profile generates Profile(nickname = "generated") orDefaultAt 100.percent
```

#### Compliant Code:

```kotlin
User::profile generates Profile(nickname = "generated")
User::profile generates default
```

#### Limitations

The rule only reports fixed probabilities attached to `generates` declarations. It intentionally skips `generatesBy`,
`generatesOneOf`, and stored probability values. `generates default` still requires the selected constructor argument
to have a default value at runtime.

Auto-correct adds `import dev.s7a.fiktion.default` and `import dev.s7a.fiktion.generates` when needed.

### PreferFixedNullProbability

This rule reports `generates ... orNullAt ...` declarations whose null probability is fixed. Use the generated value
directly for `0.0` or `0.percent`, and use `generates null` for `1.0` or `100.percent`.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<String?>() generates "value" orNullAt 0.0
type<String?>() generates "value" orNullAt 100.percent
```

#### Compliant Code:

```kotlin
type<String?>() generates "value"
type<String?>() generates null
```

#### Limitations

The `1.0` and `100.percent` replacement is only reported when the rule target is explicitly nullable in PSI, such as
`type<String?>()`, `name<String?>("name")`, or `property<User, Profile?>("profile")`, or when the generated value is
already `null`. KProperty shorthand targets such as `User::profile` are skipped for non-null generated values because
the rule does not use type resolution.

Auto-correct adds `import dev.s7a.fiktion.generates` when needed.

### PreferGeneratesByForMutableValues

This rule reports fixed mutable collection factory values that should be created by generator lambdas.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `mutableFactoryCalls` | `mutableListOf`, `mutableSetOf`, `mutableMapOf` | Factory call names whose results should be generated inside `generatesBy`. |

#### Noncompliant Code:

```kotlin
type<MutableList<String>>() generates mutableListOf("value")
type<MutableSet<String>>() generates mutableSetOf("value")
type<MutableMap<String, Int>>() generates mutableMapOf("key" to 1)
```

#### Compliant Code:

```kotlin
type<MutableList<String>>() generatesBy { mutableListOf("value") }
type<MutableSet<String>>() generatesBy { mutableSetOf("value") }
type<MutableMap<String, Int>>() generatesBy { mutableMapOf("key" to 1) }
```

#### Limitations

The rule only reports direct `mutableListOf`, `mutableSetOf`, and `mutableMapOf` calls passed to `generates`:

```kotlin
val values = mutableListOf("value")
type<MutableList<String>>() generates values
```

Auto-correct adds `import dev.s7a.fiktion.generatesBy` when needed.

### PreferGeneratesForFixedValue

This rule reports Fiktion generator expressions that always produce a fixed literal value where `generates` is clearer.
It covers constant `generatesBy` lambdas, single-literal `oneOf` lambdas, and direct single-value `generatesOneOf`
expressions such as `generatesOneOf listOf(1)` and `generatesOneOf setOf(1)`.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `constantGenerator` | `true` | Report constant `generatesBy { value }` lambdas. |
| `singleOneOf` | `true` | Report `generatesBy { oneOf(value) }` when `value` is a fixed literal. |
| `singleGeneratesOneOf` | `true` | Report direct single-value `generatesOneOf` expressions. |
| `includeNull` | `true` | Include fixed `null` values. |

#### Noncompliant Code:

```kotlin
type<Int>() generatesBy { 42 }
type<String>() generatesBy { "value" }
type<Int>() generatesBy { oneOf(1) }
type<Int>() generatesOneOf listOf(1)
```

#### Compliant Code:

```kotlin
type<Int>() generates 42
type<String>() generates "value"
type<Int>() generates 1
```

#### Limitations

The rule only reports fixed literal values, including numbers, booleans, `null`, simple signed literals, and
non-interpolated strings. It intentionally skips expressions such as `"value-$seed"`, `oneOf(value)`,
`oneOf(createValue())`, and `oneOf(listOf(1))` because converting those to fixed values can change when values are
created or what value type is generated.

Auto-correct adds `import dev.s7a.fiktion.generates` when needed.

### PreferGeneratesInForRange

This rule reports range generator lambdas such as `generatesBy { int(1..10) }` where `generatesIn 1..10` expresses the
same intent more directly.

Auto-correct adds `import dev.s7a.fiktion.generatesIn` when needed.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<Int>() generatesBy {
    int(1..10)
}
```

#### Compliant Code:

```kotlin
type<Int>() generatesIn 1..10
```

#### Limitations

The rule only autocorrects direct range generator calls whose range expression can be moved safely. It preserves imports
where possible and adds `dev.s7a.fiktion.generatesIn` when needed.

### PreferGeneratesOneOf

This rule reports `generatesBy { oneOf(a, b, ...) }` where `generatesOneOf listOf(a, b, ...)` expresses the same intent
more directly.

The rule intentionally skips single-argument `oneOf(value)` calls because Kotlin overload resolution can make
`oneOf(listOf(1))` mean either "choose this list" or "choose an element from this list" depending on the target type.

Auto-correct adds `import dev.s7a.fiktion.generatesOneOf` when needed.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<String>() generatesBy {
    oneOf("a", "b")
}
```

#### Compliant Code:

```kotlin
type<String>() generatesOneOf listOf("a", "b")
```

#### Limitations

The rule intentionally skips single-argument `oneOf(value)` calls because overload resolution can change the generated
value type. Auto-correct adds `dev.s7a.fiktion.generatesOneOf` when needed.

### PreferGroupedRuleTarget

This rule reports adjacent rules for the same target where a target block keeps related declarations together.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<User>() using FiktionConfig.String.length(10)
type<User>() generatesBy { User(id = fake()) }

property(User::id) using FiktionConfig.String.length(8)
property(User::id) generates "user-id"

name<String>("email") using FiktionConfig.String.length(24)
name<String>("email") generatesBy { "user@example.test" }
```

#### Compliant Code:

```kotlin
type<User>() {
    this using FiktionConfig.String.length(10)
    this generatesBy { User(id = fake()) }
}

property(User::id) {
    this using FiktionConfig.String.length(8)
    this generates "user-id"
}

name<String>("email").invoke {
    this using FiktionConfig.String.length(24)
    this generatesBy { "user@example.test" }
}
```

#### Limitations

The rule only groups adjacent rules with the same source-level target text and only when no comments or other statements
appear between them. It preserves the original target notation, so property references such as `User::id generates ...`
are not rewritten to `property(User::id) { ... }`.

Auto-correct adds `import dev.s7a.fiktion.invoke` when needed for grouped `type<T>()` or typed `name<T>(...)` targets.

### PreferIndexedFakeInTypeFamilyLoop

This rule reports `fake(argumentIndex)` calls inside type-family loop lambdas where the loop index should be used as
`FakeContext.index`.

#### Configuration options:

| Option | Default | Description |
| ------ | ------- | ----------- |
| `indexedLoopCalls` | Kotlin indexed collection builders and `repeat` | Call names whose lambda parameter is treated as a generated element index. |

#### Noncompliant Code:

```kotlin
typeFamily<Box<*>>() generatesBy {
    Box(List(3) { index -> fake(0) })
}
```

#### Compliant Code:

```kotlin
typeFamily<Box<*>>() generatesBy {
    Box(List(3) { index -> fake(index, argumentIndex = 0) })
}
```

#### Limitations

The rule only autocorrects direct integer literal calls such as `fake(0)` inside single-parameter or implicit-`it` loop
lambdas. It intentionally leaves `fakeElement`, `fakeKey`, and `fakeValue` unchanged.

### PreferInfixFiktionDsl

This rule reports Fiktion DSL declarations written with dot-call syntax when the infix DSL form is available. Prefer the
infix form so generated code follows the same Fiktion style everywhere.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<String>().generates("value")
property(User::id).generatesBy { "user-id" }
this.using(FiktionConfig.String.length(8))
```

#### Compliant Code:

```kotlin
type<String>() generates "value"
property(User::id) generatesBy { "user-id" }
this using FiktionConfig.String.length(8)
```

#### Limitations

The rule autocorrects dot calls on recognized Fiktion rule targets, callable property references, and `this`. It skips
other receivers to avoid rewriting unrelated APIs with the same method names.

### PreferNamedTypeFamilyFakeArgumentIndex

This rule reports type-family `fake`, `fakeElement`, `fakeKey`, and `fakeValue` helper calls that pass the second
`argumentIndex` parameter positionally. Prefer the named argument so generated code does not confuse the loop index with
the requested type-argument index.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
typeFamily<Pair<*, *>>() generatesBy {
    Pair(fake(0, 0), fake(0, 1))
}
```

#### Compliant Code:

```kotlin
typeFamily<Pair<*, *>>() generatesBy {
    Pair(fake(0, argumentIndex = 0), fake(0, argumentIndex = 1))
}
```

#### Limitations

The rule autocorrects direct positional second arguments on type-family fake helper calls inside `generatesBy` bodies.
It does not report calls outside type-family generators.

### PreferPropertyShorthand

This rule reports `property(User::id)` rule targets where the KProperty shorthand `User::id` can be used directly.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
property(User::id) generates "user-id"
property(User::id) using FiktionConfig.String.length(8)
```

#### Compliant Code:

```kotlin
User::id generates "user-id"
User::id using FiktionConfig.String.length(8)
```

#### Limitations

The rule only rewrites direct `property(User::id)` targets. Auto-correct preserves the original operation and imports.

### PreferRuleTargetDeclarationOrder

This rule reports grouped target blocks where generator declarations appear before config declarations. Declare configs
first, then generators, so blocks have a stable order.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
type<String> {
    this generates "value"
    this using FiktionConfig.String.length(8)
}
```

#### Compliant Code:

```kotlin
type<String> {
    this using FiktionConfig.String.length(8)
    this generates "value"
}
```

#### Limitations

The rule autocorrects simple grouped target blocks that contain only `this using ...` and `this generates...`
declarations. When other statements are mixed into the block, it reports the ordering issue but leaves the code
unchanged.

### PreferSeedParameter

This rule reports `fake<T> { withSeed(...) }` when a single seed declaration in the fake block can be passed directly as
`fake<T>(seed = ...)`. Blocks with multiple `withSeed` declarations are left to `AvoidMultipleSeedsInFakeSpec`.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fake<User> {
    User::id generates "user-id"
    this withSeed 123
}
```

#### Compliant Code:

```kotlin
fake<User>(seed = 123) {
    User::id generates "user-id"
}
```

#### Limitations

The rule is report-only because it does not currently rewrite the `fake` call and remove the block statement. It reports
only blocks with exactly one `withSeed` declaration; blocks with multiple seed declarations are handled by
`AvoidMultipleSeedsInFakeSpec`.

### PreferThisUsingInFakeSpec

This rule reports bare `using(...)` calls in Fiktion spec blocks. Use `this using ...` so config declarations have the
same explicit receiver shape as generated grouped target blocks.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
fake<String> {
    using(FiktionConfig.String.length(8))
}
```

#### Compliant Code:

```kotlin
fake<String> {
    this using FiktionConfig.String.length(8)
}
```

#### Limitations

The rule autocorrects single-argument bare `using(...)` calls. Two-argument overloads such as `using(key, value)` are
skipped because preserving the exact config call shape requires more context.

### PreferTypeRuleForNonGeneric

This rule reports `typeFamily<T>()` calls that do not need type-family matching because `T` is not generic.

#### Configuration options:

This rule has no configuration options.

#### Noncompliant Code:

```kotlin
typeFamily<Boolean>() generatesBy { true }
```

#### Compliant Code:

```kotlin
type<Boolean>() generatesBy { true }
```

#### Limitations

The rule intentionally does not report generic type-family targets:

```kotlin
typeFamily<List<String>>() generatesBy { listOf("value") }
typeFamily<Box<*>>() generatesBy { Box(fake(0)) }
```

It also skips generators that call type-family fake helpers, because the target type may be a typealias for a generic type:

```kotlin
typealias UserList = List<User>

typeFamily<UserList>() generatesBy {
    @Suppress("UNCHECKED_CAST")
    fake(0) as UserList
}
```

Auto-correct adds `import dev.s7a.fiktion.type` when needed.

## Default Configuration

The following `detekt.yml` shows the default rule settings explicitly, so copy only the entries you want to customize:

```yaml
fiktion:
  # Avoid element, key, or value targets on star-projected container targets.
  AvoidContainerPartOnStarProjectedUnknown:
    active: true

  # Avoid oneOf calls that cannot select a value.
  AvoidEmptyOneOf:
    active: true

  # Avoid global Fiktion.configure calls inside local test functions.
  AvoidGlobalConfigureInLocalTest:
    active: true
    testAnnotations:
      - Test
      - kotlin.test.Test
      - org.junit.Test
    testNamePrefixes:
      - test

  # Avoid stored Fiktion.configure snapshots without restore calls.
  AvoidGlobalFiktionConfigureWithoutRestore:
    active: true

  # Avoid statically invalid Fiktion config ranges.
  AvoidInvalidConfigRange:
    active: true

  # Avoid fake(index) calls that exceed type-family type arguments.
  AvoidInvalidTypeFamilyFakeIndex:
    active: true

  # Avoid overriding a config key for the same rule target.
  AvoidMultipleConfigsForRuleTarget:
    active: true

  # Avoid overriding a generator for the same rule target.
  AvoidMultipleGeneratorsForRuleTarget:
    active: true

  # Avoid multiple withSeed declarations in the same Fiktion spec block.
  AvoidMultipleSeedsInFakeSpec:
    active: true

  # Avoid rule targets that do not explicitly target a property.
  AvoidNonPropertyRuleTargets:
    active: false

  # Avoid Random instances inside generator lambdas.
  AvoidRandomInstanceInGenerator:
    active: true

  # Avoid fake<T>() calls that can recursively generate the same type.
  AvoidRecursiveFakeInGenerator:
    active: true

  # Avoid registering Fiktion rules from loop bodies.
  AvoidRuleDeclarationsInLoops:
    active: true
    loopCalls:
      - Array
      - BooleanArray
      - ByteArray
      - CharArray
      - DoubleArray
      - FloatArray
      - IntArray
      - List
      - LongArray
      - MutableList
      - ShortArray
      - UByteArray
      - UIntArray
      - ULongArray
      - UShortArray
      - repeat
      - all
      - any
      - associate
      - associateBy
      - associateWith
      - count
      - filter
      - filterIndexed
      - flatMap
      - fold
      - forEach
      - forEachIndexed
      - map
      - mapIndexed
      - none
      - onEach
      - reduce
      - sumOf

  # Avoid discarding Fiktion.configure snapshots.
  AvoidUnusedFiktionSnapshot:
    active: true

  # Avoid configured calls inside generator lambdas.
  ForbiddenGeneratorCall:
    active: true
    calls:
      - Clock.System.now
      - Date
      - File.readBytes
      - File.readText
      - Files.readAllBytes
      - Files.readString
      - Instant.now
      - LocalDate.now
      - LocalDateTime.now
      - ProcessBuilder
      - Random.nextBoolean
      - Random.nextBytes
      - Random.nextDouble
      - Random.nextFloat
      - Random.nextInt
      - Random.nextLong
      - Runtime.getRuntime
      - System.currentTimeMillis
      - System.getenv
      - System.getProperty
      - System.nanoTime
      - Thread.sleep
      - UUID.randomUUID
      - assertContains
      - assertContentEquals
      - assertEquals
      - assertFails
      - assertFailsWith
      - assertFalse
      - assertIs
      - assertIsNot
      - assertNotEquals
      - assertNotNull
      - assertNotSame
      - assertNull
      - assertSame
      - assertTrue
      - fail
      - java.io.File.readBytes
      - java.io.File.readText
      - java.lang.System.currentTimeMillis
      - java.lang.System.getenv
      - java.lang.System.getProperty
      - java.lang.System.nanoTime
      - java.lang.Thread.sleep
      - java.nio.file.Files.readAllBytes
      - java.nio.file.Files.readString
      - java.time.Instant.now
      - java.time.LocalDate.now
      - java.time.LocalDateTime.now
      - java.util.Date
      - java.util.UUID.randomUUID
      - kotlin.random.Random.nextBoolean
      - kotlin.random.Random.nextBytes
      - kotlin.random.Random.nextDouble
      - kotlin.random.Random.nextFloat
      - kotlin.random.Random.nextInt
      - kotlin.random.Random.nextLong
      - kotlin.time.Clock.System.now
      - kotlin.time.Instant.now
      - kotlinx.datetime.Clock.System.now
      - kotlinx.datetime.Instant.now

  # Prefer configureCollection or configureMap for container materialization rules.
  PreferContainerConfigureHelpers:
    active: true
    containerMaterializationCalls:
      - Array
      - List
      - MutableList
      - buildList
      - listOf
      - mapOf
      - mutableListOf
      - mutableMapOf
      - mutableSetOf
      - setOf
      - toList
      - toMap
      - toMutableList
      - toMutableMap
      - toMutableSet
      - toSet

  # Prefer fakeElement, fakeKey, and fakeValue in type-family container generators.
  PreferContainerPartFakeHelpers:
    active: true
    indexedLoopCalls:
      - Array
      - BooleanArray
      - ByteArray
      - CharArray
      - DoubleArray
      - FloatArray
      - IntArray
      - List
      - LongArray
      - MutableList
      - ShortArray
      - UByteArray
      - UIntArray
      - ULongArray
      - UShortArray
      - repeat

  # Prefer explicit seed names for fake calls.
  PreferExplicitFakeSeedName:
    active: true

  # Prefer fixed config values over equal-bound config ranges.
  PreferFixedConfigValue:
    active: true

  # Prefer fixed generated values over zero or full default probabilities.
  PreferFixedDefaultProbability:
    active: true

  # Prefer fixed generated values over zero or full null probabilities.
  PreferFixedNullProbability:
    active: true

  # Prefer generator lambdas for mutable values.
  PreferGeneratesByForMutableValues:
    active: true
    mutableFactoryCalls:
      - mutableListOf
      - mutableSetOf
      - mutableMapOf

  # Prefer fixed values over generators that always produce a fixed value.
  PreferGeneratesForFixedValue:
    active: true
    constantGenerator: true
    singleOneOf: true
    singleGeneratesOneOf: true
    includeNull: true

  # Prefer range-specific generators.
  PreferGeneratesInForRange:
    active: true

  # Prefer oneOf-specific generators.
  PreferGeneratesOneOf:
    active: true

  # Prefer grouping repeated declarations for the same target.
  PreferGroupedRuleTarget:
    active: true

  # Prefer passing loop indexes to fake calls in type-family loops.
  PreferIndexedFakeInTypeFamilyLoop:
    active: true
    indexedLoopCalls:
      - Array
      - BooleanArray
      - ByteArray
      - CharArray
      - DoubleArray
      - FloatArray
      - IntArray
      - List
      - LongArray
      - MutableList
      - ShortArray
      - UByteArray
      - UIntArray
      - ULongArray
      - UShortArray
      - repeat

  # Prefer infix Fiktion DSL declarations over dot-call syntax.
  PreferInfixFiktionDsl:
    active: true

  # Prefer named argumentIndex in type-family fake helper calls.
  PreferNamedTypeFamilyFakeArgumentIndex:
    active: true

  # Prefer KProperty shorthand targets.
  PreferPropertyShorthand:
    active: true

  # Prefer configs before generators in grouped target blocks.
  PreferRuleTargetDeclarationOrder:
    active: true

  # Prefer fake(seed = ...) over withSeed inside a fake block.
  PreferSeedParameter:
    active: true

  # Prefer this using ... over bare using(...) calls.
  PreferThisUsingInFakeSpec:
    active: true

  # Prefer type<T>() over non-generic typeFamily<T>().
  PreferTypeRuleForNonGeneric:
    active: true
```
