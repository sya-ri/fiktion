# fiktion

Fiktion is a Kotlin Multiplatform fake data library for tests.

It is designed for the case where a test needs realistic-enough object graphs quickly, but still needs precise control when a specific property matters.

```kotlin
val id = fake<String>()

val user = Fiktion {
    type<User>() generatesBy {
        User(id = "user-${random.nextLong()}")
    }
}.fake<User>()
```

The main code under test does not need Fiktion annotations or helper functions. Fiktion is intended to be added from tests and configured from the outside.

## Current Status

This repository currently contains the project skeleton, public API shape, and the first runtime generation path. Primitive built-ins, explicit type rules, isolated `Fiktion` instances, global configuration snapshots, seeds, and generator helpers are implemented and covered by common tests.

Object graph construction from Kotlin metadata, compiler plugin behavior, Gradle plugin behavior, default-value probabilities, and collection/map generation are still under development.

Examples marked as planned depend on metadata/compiler work that is not implemented yet.

Local `./gradlew build` skips browser test execution unless `-Pfiktion.enableBrowserTests=true` is provided. CI enables browser tests and installs Chrome before running Gradle.

## Basic Usage

Generate primitive fake data with the top-level `fake<T>()` function:

```kotlin
val text = fake<String>()
val count = fake<Int>()
```

Each call is expected to produce fresh fake data by default.

The current runtime path applies custom generators through explicit type rules. Custom generators receive a runtime
context with a random instance, current type, recursion depth, and seed when one is provided. Property and path context
are available when the generation request contains property metadata. Automatic object graph construction will provide
that metadata once it is implemented.

## Planned Per-Call Rules

Use the `fake<T> { ... }` block to override generation for one call. Property-level examples in this section describe
the intended object graph API and depend on metadata-based object construction.

```kotlin
val user = fake<User> {
    User::id generates "user-1"
    User::displayName generates "Test User"
}
```

Per-call rules are scoped to the generated root type. This is intentional: `fake<User> { ... }` should not expose global owner-qualified rule APIs that can accidentally configure unrelated models.

Nested values can be configured directly from the parent property:

```kotlin
val user = fake<User> {
    User::profile {
        Profile::nickname generates "example"
    }
}
```

Specific nested properties can also be targeted with `/`:

```kotlin
val user = fake<User> {
    (User::profile / Profile::nickname) generates "example"
}
```

Property-name matching is available in the per-call scope for dynamic rules within the generated graph:

```kotlin
val user = fake<User> {
    name("id") generates "user-1"
    name(".*Name".toRegex()) generatesBy { "generated-name" }
}
```

## Global Rules

Use `Fiktion.configure` to update the global configuration and receive a snapshot that can restore the previous configuration.
The explicit type examples below work in the current runtime path. Property and name targets are part of the object graph
rule surface and become useful when object graph construction provides property metadata.

```kotlin
val snapshot = Fiktion.configure {
    type<User>() generatesBy {
        User(id = "user-${random.nextLong()}")
    }

    type<Order>() generatesBy {
        Order(id = "order-${random.nextLong()}")
    }

    // Planned object graph rule: *.email: String
    name<String>("email") generatesBy {
        "test-${random.nextInt()}@example.test"
    }
}

try {
    val user = fake<User>()
} finally {
    check(snapshot.restore())
}
```

Use `snapshot.restore(force = true)` only when the previous configuration must be restored even if another configuration
was installed after this snapshot.

Unrestricted owner-qualified rule APIs are available from Fiktion configuration, not from `fake<T> { ... }`.

## Isolated Fiktion Instances

Create an isolated Fiktion instance when a test suite needs a named rule set instead of changing global configuration:

```kotlin
val fiktion = Fiktion {
    type<User>() generatesBy {
        User(id = "api-user-${random.nextLong()}")
    }
}

val user = fiktion.fake<User>()
```

## Targets

The global configuration scope supports type targets now and exposes planned property and name targets for generated
object graphs:

```kotlin
type<User>() generatesBy { /* any User */ }
property<User, String>() generatesBy { /* any String property owned by User */ }
property<User, String>("id") generates "user-1"
name<String>("id") generates "shared-id"
name("email") generatesBy { "test-${random.nextInt()}@example.test" }
property<User, String>("id") generates "user-1"
property(User::profile / Profile::nickname) generates "example"
```

Bare property-reference rules such as `User::id generates "user-1"` are intentionally unavailable in global and
isolated configuration scopes because Kotlin common code cannot recover the owner and value type from the reference
alone. Use `property<User, String>("id")` there. The API dump tracks those low-level entry points as
`DeprecationLevel.ERROR`.

Rule precedence is intended to be:

1. Per-call rules
2. Fiktion instance rules
3. Global rules
4. Add-on rules
5. Built-in primitive rules

Within the same precedence level, more specific targets win before registration order. When two matching rules have the
same specificity, the later registration wins.

## Planned Defaults And Nulls

Rules can opt into null generation with probabilities when the requested value type is nullable. Default-value
probabilities are recorded for the object construction path and are not applied by the current primitive runtime path.

```kotlin
val user = fake<User> {
    User::nickname.autoGenerates() orNullAt 0.3
    User::status.autoGenerates() orDefaultAt 30.percent
}
```

`Double` probabilities use `0.0..1.0`. Percentage helpers are available with `percent`.
When a name rule intentionally generates `null`, declare the value type explicitly, for example `name<String?>("nickname") generates null` or `name<String?>("nickname") generatesBy { null }`.

## Planned Collections And Maps

Collection and map sizes are configured separately from element generation:

```kotlin
val catalog = fake<Catalog> {
    Catalog::items generatesEach {
        fake<Item>()
    } withSize 3

    Catalog::tags generatesEach {
        string(8)
    } withSize (1..5)
}
```

Map key and value generation can be configured separately or completed as an entry rule:

```kotlin
val index = fake<SearchIndex> {
    SearchIndex::entries generatesKeys { string(8) }
    SearchIndex::entries generatesValues { fake<Entry>() }

    SearchIndex::aliases
        .generatesKeys { string(6) }
        .andValues { oneOf(listOf("primary", "secondary")) }
}
```

## Realistic Data

Fiktion core focuses on object graph generation, rule resolution, and Kotlin metadata. Strict domain data such as names, email addresses, postal addresses, or localized text is expected to come from custom rules, add-ons, or libraries such as Datafaker.

```kotlin
val faker = Faker()

val user = fake<User> {
    User::email generatesBy {
        faker.internet().emailAddress()
    }
}
```

## Add-Ons

Add-ons contribute reusable rules for external libraries or project-specific types.

```kotlin
public object KotlinxDatetimeFiktion : FiktionAddon {
    override val id: String = "dev.s7a.fiktion.kotlinx-datetime"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<Instant>() generatesBy {
                // Add-on-provided generation rule.
                TODO()
            }
        }
    }
}
```

Installed add-ons are expected to sit below explicit per-call, instance, and global rules in precedence.

## Gradle Direction

The Gradle plugin is intended to enable metadata generation for test source sets by default, with opt-in support for main/runtime source sets.

```kotlin
plugins {
    id("dev.s7a.fiktion")
}

fiktion {
    sourceSets {
        named("commonTest") {
            // Enabled by default for tests.
        }

        named("commonMain") {
            enabled = true
        }
    }
}
```

Exact Gradle DSL names may change while the skeleton is refined.
