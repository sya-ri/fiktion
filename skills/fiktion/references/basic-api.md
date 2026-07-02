# Fiktion Basic API

## Install

Typical JVM test setup:

```kotlin
plugins {
    kotlin("jvm") version "2.4.0"
    id("dev.s7a.fiktion") version "0.5.1"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:0.5.1")
    testImplementation("dev.s7a:fiktion-addon-java:0.5.1") // optional JVM add-on
    testImplementation("dev.s7a:fiktion-addon-kotlinx-datetime:0.5.1") // optional kotlinx-datetime add-on
    detektPlugins("dev.s7a:fiktion-detekt-rules:0.5.1") // optional detekt rules
}
```

The Gradle plugin enables Fiktion for test source sets by default, including JVM `test` and Multiplatform `commonTest` / `jvmTest`.

Opt in main source sets explicitly:

```kotlin
fiktion {
    sourceSet("commonMain") {
        enabled.set(true)
    }
}
```

Project-wide knobs:

```kotlin
fiktion {
    enabled.set(true)      // every Kotlin source set
    testEnabled.set(false) // disable default test-source-set behavior
    sourceSet("jvmTest") {
        enabled.set(true)
    }
}
```

## Generate Values

```kotlin
val text = fake<String>()
val count = fake<Int>()
val user = fake<User>()
val deterministic = fake<User>(seed = 123)
```

Per-call overrides apply only to that generated graph:

```kotlin
val user = fake<User> {
    withSeed 123
    User::id generates "user-1"
    User::displayName generatesBy { "User ${int(1, 999)}" }
}
```

Use an isolated generator when a suite needs shared local rules:

```kotlin
val fiktion = Fiktion {
    type<User>() generatesBy {
        User(id = "api-user-${random.nextLong()}")
    }
}

val user = fiktion.fake<User>()
```

The same `fake<T>(seed = ...)` and `fake<T> { ... }` forms exist on isolated instances:

```kotlin
val user = fiktion.fake<User>(seed = 123) {
    User::id generates "user-1"
}
```

Use global configuration sparingly and restore it:

```kotlin
val snapshot = Fiktion.configure {
    name<String>("email") generatesBy {
        "test-${random.nextInt()}@example.test"
    }
}

try {
    fake<User>()
} finally {
    check(snapshot.restore())
}
```

`Fiktion.configure` updates process-wide global rules for top-level `fake<T>()` calls and for isolated `Fiktion()`
instances created from the global base configuration. Use it for shared defaults such as IDs, emails, clock-like values,
library add-on rules, or metadata that project-level test helpers should see. Prefer per-call rules for one test and
`Fiktion { ... }` for class-local or suite-local generators that do not need global state.

Always keep the returned `FiktionSnapshot` and restore it from the matching teardown hook. `restore()` returns `false`
if a newer global configuration was installed after this snapshot; `restore(force = true)` overwrites newer global
configuration and should be limited to cleanup code that owns the process or project-level configuration.

## Test Framework Integration

Prefer `Fiktion { ... }` for test-, class-, or spec-scoped defaults. Use `Fiktion.configure` only when top-level
`fake<T>()` calls intentionally need global rules. Since global configuration mutates process-wide state, keep it at
project-level setup when tests may run in parallel.

```kotlin
// kotlin.test: prefer an isolated instance from @BeforeTest.
private lateinit var fiktion: Fiktion

@BeforeTest
fun configureFiktion() {
    fiktion = Fiktion {
        name<String>("email") generatesBy {
            "user-${random.nextLong()}@example.test"
        }
    }
}

@Test
fun `creates a user`() {
    fiktion.fake<User>()
}
```

```kotlin
// JUnit 5: @TestInstance(PER_CLASS) is class-scoped, so prefer an isolated instance.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {
    private lateinit var fiktion: Fiktion

    @BeforeAll
    fun configureFiktion() {
        fiktion = Fiktion {
            name<String>("email") generatesBy {
                "${string(length = 12)}@example.test"
            }
        }
    }

    @Test
    fun `finds user by email`() {
        fiktion.fake<User>()
    }
}
```

```kotlin
// JUnit 5: use the root extension store when global top-level fake<T>() defaults are intentional.
class FiktionExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        context.root
            .getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent(FiktionResource::class.java) {
                FiktionResource()
            }
    }

    private class FiktionResource : AutoCloseable {
        private val snapshot =
            Fiktion.configure {
                name<String>("email") generatesBy {
                    "user-${random.nextLong()}@example.test"
                }
            }

        override fun close() {
            check(snapshot.restore(force = true))
        }
    }
}
```

```kotlin
// Kotest: prefer an isolated instance for spec-scoped defaults.
// Use the same pattern with StringSpec, FunSpec, DescribeSpec, FreeSpec, ShouldSpec, or BehaviorSpec.
class UserRepositorySpec : StringSpec({
    val fiktion = Fiktion {
        name<String>("email") generatesBy {
            "user-${random.nextLong()}@example.test"
        }
    }

    "finds user by email" {
        fiktion.fake<User>()
    }
})
```

```kotlin
// Kotest: use io.kotest.provided.ProjectConfig only when global top-level fake<T>() defaults are intentional.
class ProjectConfig : AbstractProjectConfig() {
    private lateinit var fiktionSnapshot: FiktionSnapshot

    override suspend fun beforeProject() {
        fiktionSnapshot = Fiktion.configure {
            name<String>("email") generatesBy {
                "user-${random.nextLong()}@example.test"
            }
        }
    }

    override suspend fun afterProject() {
        check(fiktionSnapshot.restore(force = true))
    }
}
```

## Rule Targets

Per-call blocks can use property references:

```kotlin
fake<User> {
    User::id generates "user-1"
    User::age generatesIn 18..99
    User::status generatesOneOf listOf(Status.Active, Status.Pending)
}
```

Nested object configuration can be block-based or path-based:

```kotlin
fake<User> {
    User::profile {
        Profile::nickname generates "example"
        Profile::avatar {
            Avatar::url generates "https://example.test/avatar.png"
        }
    }

    (User::profile / Profile::nickname) generates "example"
    (User::profile / Profile::roles) {
        this using FiktionConfig.Collection.size(2)
    }
}
```

You can also target an explicit path object:

```kotlin
fake<User> {
    property(User::profile / Profile::nickname) generates "example"
}
```

Global, isolated, and add-on scopes can use KProperty infix functions or reified targets:

```kotlin
Fiktion {
    type<User>() generatesBy { User(id = string()) }
    User::id generates "user-1"
    property<User, String>("id") generates "user-1"
    property<User, String>() generatesBy { string(length = 8) }
    property<User, String>(".*Name".toRegex()) generatesBy { string() }
    property(User::profile / Profile::nickname) generates "example"
    name<String>("email") generatesBy { "test@example.test" }
    name("id") generatesBy { "id-${int(1, 999)}" } // value type inferred from generator
}
```

Prefer reified targets in shared configuration when they make the owner/value type obvious, especially for name rules or regex rules.

Property rules can depend on direct constructor properties generated earlier for the same object:

```kotlin
fake<User> {
    User::id generates "user-1"
    User::email.dependsOn(User::id) generatesBy { id ->
        "$id@example.test"
    }
}
```

Multiple dependencies are passed to the generator in declaration order:

```kotlin
fake<Profile> {
    Profile::displayName.dependsOn(Profile::firstName, Profile::lastName) generatesBy { first, last ->
        "$first $last"
    }
}
```

Use `dependsOn` only for direct constructor properties of the same generated object. The dependency must be generated
before the dependent property; if the dependency used a constructor default value, Fiktion cannot observe that value and
generation fails. The typed overloads should share the same registration path as `dependsOn(vararg dependencies)` so
typed and untyped dependency rules behave consistently.

Type-family targets are for generic families:

```kotlin
Fiktion {
    typeFamily<Box<*>>() generatesBy {
        Box(fake(0))
    }
}
```

## Rule Values

Common rule forms:

```kotlin
target generates value
target generatesBy { /* FakeContext receiver */ }
target generates auto
target generates default
target generatesIn 1..10
target generatesOneOf listOf("a", "b")
target generates auto excluding SomeEnum.Deprecated
target generates auto excluding { value -> value == SomeEnum.Deprecated }
target generates value withSeed 123
target generates value orNullAt 0.3
target generates value orDefaultAt 30.percent
```

Use `excluding` only for selection-based generators: enum entries, sealed subtypes, and `generatesOneOf` candidates.
Multiple exclusions are cumulative, and generation fails with `FiktionConfigurationException` when every candidate is
excluded.

```kotlin
fake<User> {
    User::status generates auto excluding Status.Deleted excluding Status.Suspended
    User::status generates auto excluding { status -> status.name.startsWith("Deprecated") }
    User::status generatesOneOf listOf(Status.Active, Status.Pending, Status.Deleted) excluding Status.Deleted
}
```

For sealed types, exclude subtypes with `KClass`, `KType`, or a `KType` predicate:

```kotlin
fake<Message> {
    type<Message>() generates auto excluding ImageMessage::class
    type<Message>() generates auto excluding typeOf<BoxMessage<String>>()
    type<Message>() generates auto excluding { type -> type.classifier == InternalMessage::class }
}
```

Automatic nullable values generate either a non-null value or `null` with 50% probability. Automatic defaultable
constructor arguments generate either an automatic value or the constructor default with 50% probability. `generates
value` always returns that value unless `orNullAt` or `orDefaultAt` is set. `Double` probabilities use `0.0..1.0`;
`percent` helpers are available. `generates default` requires a constructor argument with a default value.

`withSeed` exists at two levels:

```kotlin
fake<User> {
    withSeed 123
    User::id generatesBy { "user-$seed" } withSeed 456
}
```

When intentionally generating null for a name rule, make the value type explicit:

```kotlin
name<String?>("nickname") generates null
name<String?>("nickname") generatesBy { null }
```

## Equivalent And Related Forms

Fiktion intentionally offers several ways to express the same or nearby intent. Prefer the narrowest target that still
matches the test's intent.

### Fixed Value

Per-call root property forms. The property-reference form targets only `User.id`; name forms match any generated
property named `id` in the current graph whose value type matches the rule:

```kotlin
fake<User> {
    User::id generates "user-1"
    name<String>("id") generates "user-1"
    name("id") generates "user-1"
}
```

Shared configuration forms. `User::id` and `property<User, String>("id")` are equivalent in target breadth; name forms
are broader because they are not owner-specific:

```kotlin
Fiktion {
    User::id generates "user-1"
    property<User, String>("id") generates "user-1"
    name<String>("id") generates "user-1"
    name("id") generates "user-1"
}
```

Use the property-reference form when possible. Use `property<User, String>("id")` when owner and value type should both
be explicit. Use `name<String>("id")` when the convention should intentionally apply across owners.

For value classes, choose the rule by the surface you want to control:

```kotlin
@JvmInline
value class PublicUserId(val value: String)

fake<PublicUserId> {
    PublicUserId::value generates "user-1"
}

@JvmInline
value class PrivateUserId(private val value: String)

fake<PrivateUserId> {
    name("value") generates "user-1"
}

Fiktion {
    type<PublicUserId>() generates PublicUserId("user-1")
}
```

Use a property reference for a public underlying property. Use a name rule for a private underlying property because
external Kotlin code cannot reference it. Use a type rule when the whole value object should be replaced.

### Generator Function

Per-call root property forms. As with fixed values, property-reference targets are owner-specific and name targets are
broader:

```kotlin
fake<User> {
    User::id generatesBy { "user-${int(1, 999)}" }
    name<String>("id") generatesBy { "user-${int(1, 999)}" }
    name("id") generatesBy { "user-${int(1, 999)}" }
}
```

Shared configuration forms:

```kotlin
Fiktion {
    User::id generatesBy { "user-${int(1, 999)}" }
    property<User, String>("id") generatesBy { "user-${int(1, 999)}" }
    name<String>("id") generatesBy { "user-${int(1, 999)}" }
    name("id") generatesBy { "user-${int(1, 999)}" }
}
```

### Nested Property

These both target `User.profile.nickname`:

```kotlin
fake<User> {
    User::profile {
        Profile::nickname generates "example"
    }

    (User::profile / Profile::nickname) generates "example"
}
```

The block style is easier when configuring several properties under the same object. The path style is terser for one
leaf.

### Automatic Collection Generation

Per-call property form:

```kotlin
fake<Team> {
    Team::names {
        this using FiktionConfig.Collection.size(3)
    }
}
```

Shared configuration equivalents:

```kotlin
Fiktion {
    Team::names {
        this using FiktionConfig.Collection.size(3)
    }
    property<Team, List<String>>("names") {
        this using FiktionConfig.Collection.size(3)
    }
}
```

For nested collections:

```kotlin
fake<Department> {
    Department::team {
        Team::names {
            this using FiktionConfig.Collection.size(3)
        }
    }

    (Department::team / Team::names) {
        this using FiktionConfig.Collection.size(3)
    }
}
```

### Map Keys And Values

Configure keys and values through map container targets:

```kotlin
fake<SearchIndex> {
    SearchIndex::entries {
        key generatesBy { string(length = 8) }
        value generatesBy { Entry(id = string(length = 8)) }
    }
}
```

Use `property(SearchIndex::entries).key` and `property(SearchIndex::entries).value` when the declarations need to live
outside a block.

### Scoped Configuration

These use the same rule language at different precedence levels:

```kotlin
val user = fake<User> {
    User::id generates "user-1"
}

val fiktion = Fiktion {
    User::id generates "user-1"
}
val userFromInstance = fiktion.fake<User>()

val snapshot = Fiktion.configure {
    User::id generates "user-1"
}
```

Prefer per-call rules for test-specific intent, isolated `Fiktion { ... }` for reusable suite-local policy, and global
configuration only for process-wide test defaults.

### Generator Defaults

Use typed generator configuration when the built-in or add-on generator should stay in place but use different ranges,
sizes, or formatting defaults:

```kotlin
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.invoke

val user = fake<User> {
    User::id using FiktionConfig.String.length(12)
}

val values = fake<List<Int>> {
    this using FiktionConfig.Collection.size(5)
    element using FiktionConfig.Int.range(10..20)
}

val labels = fake<Map<String, List<Int>>> {
    this using FiktionConfig.Map.size(2)
    key using FiktionConfig.String.length(4)
    value.element using FiktionConfig.Int.range(10..20)
}

val groups = fake<List<Map<String, Int>>> {
    element {
        key using FiktionConfig.String.length(4)
        value using FiktionConfig.Int.range(10..20)
    }
}

val catalog = fake<Catalog> {
    property(Catalog::counts).element using FiktionConfig.Int.range(10..20)
}

val indexed = fake<Map<String, Int>> {
    key generatesBy { "key-$index" }
    value generatesBy { index }
}

val fiktion = Fiktion {
    this using FiktionConfig.Int.range(-200..200)
    this using FiktionConfig.Collection.size(3)
}
```

`this using ...` is available as a member inside `fake` and `Fiktion` configuration lambdas, so callers do not need a
separate `using` import for root config. The `FiktionConfig.Collection.size(5)` shorthand uses Fiktion's `invoke`
operator extension, so import `dev.s7a.fiktion.invoke` or use `import dev.s7a.fiktion.*`. Use
`FiktionConfig.Collection.size` for `List`, `Set`, and other collection types, and `FiktionConfig.Map.size` for maps.

When a config key accepts a range, prefer the fixed-value shorthand for equal bounds:

```kotlin
this using FiktionConfig.Collection.size(2)
this using FiktionConfig.Int.range(42)
```

instead of:

```kotlin
this using FiktionConfig.Collection.size(2..2)
this using FiktionConfig.Int.range(42..42)
```

Global, instance, and add-on builder configs apply to every generated value matching the config key's scope. Per-call
root config applies only when the config scope can affect the generated root type. Property config applies only to that
property path.

Generator config is intentionally separate from rules: rules define how a value is generated, while config changes
parameters read by the existing generator. Container targets can be chained through generated collection and map parts,
so `fake<Map<String, List<Int>>> { value.element using FiktionConfig.Int.range(10..20) }` configures only the generated
`Int` elements below map values. Container targets can also be grouped with blocks, such as
`fake<List<Map<String, Int>>> { element { key using FiktionConfig.String.length(4) } }`. Map keys and values can also
be defined through `key generatesBy { ... }` and `value generatesBy { ... }`.

## Collections And Maps

Collections:

```kotlin
fake<Catalog> {
    Catalog::items {
        this using FiktionConfig.Collection.size(3)
    }
    Catalog::tags {
        this using FiktionConfig.Collection.size(1..5)
        element generatesBy { string(length = 8) }
    }
}
```

Maps:

```kotlin
fake<SearchIndex> {
    SearchIndex::entries {
        this using FiktionConfig.Map.size(2)
        key generatesBy { string(length = 8) }
        value generatesBy { Entry(id = string(length = 8)) }
    }
    SearchIndex::weights {
        key generatesBy { string(length = 8) }
        value generatesBy { int(1, 100) }
    }
}
```

For custom collection/map concrete types, configure conversion once:

```kotlin
Fiktion {
    configureCollection<CustomCollection<*>> { elements ->
        CustomCollection(elements)
    }
    configureMap<CustomMap<*, *>> { entries ->
        CustomMap(entries.toMap())
    }
}
```

## Metadata And Compiler Plugin

The compiler plugin generates metadata for Kotlin types so Fiktion can construct objects without annotations.

Supported shapes include:

- regular classes and data classes with supported primary constructors
- value classes with one constructor value
- enum classes
- sealed classes and sealed interfaces
- singleton objects and companion objects

Skipped shapes should be configured explicitly:

- abstract classes and interfaces
- fun interfaces and annotation classes
- inner classes and local classes
- classes without a primary constructor
- private or protected primary constructors
- vararg or unsupported constructor parameters

Explicit rule example:

```kotlin
Fiktion {
    type<PrivateUser>() generatesBy {
        PrivateUser.create("user-1")
    }
}
```

Manual metadata registration exists for advanced tests and compiler-plugin work:

```kotlin
Fiktion {
    register(userMetadata())
}
```

`register(...)`, `FiktionObjectMetadata`, `FiktionValueMetadata`, `FiktionEnumMetadata`, `FiktionSealedMetadata`,
`FiktionArrayMetadata`, and `generatedArray(...)` are experimental APIs. Prefer compiler-generated metadata in normal
user code.

## Coverage Notes

This reference documents the normal public, non-deprecated DSL styles:

- top-level and instance `fake`
- `Fiktion { ... }` and `Fiktion.configure`
- per-call `FakeSpec`
- type, type-family, property, path, and name targets
- fixed, generator, auto, range, one-of, nullable, default, seed, collection, and map rules
- add-on converters and installation

It intentionally does not recommend deprecated low-level `KType` overloads. Use them only when maintaining legacy code or
when an existing call site already carries a `KType`; otherwise use the reified overloads.

## Precedence

Highest to lowest:

1. Per-call rules
2. Fiktion instance rules
3. Global rules
4. Add-on rules
5. Built-in rules

Within the same layer, more specific matchers win; when specificity is equal, later registration wins.
