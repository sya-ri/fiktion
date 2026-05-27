# fiktion

Fiktion is a Kotlin Multiplatform fake data library for tests.

It is designed for the case where a test needs realistic-enough object graphs quickly, but still needs precise control when a specific property matters.

```kotlin
val user = fake<User>()

val fixedUser = fake<User> {
    User::name generates "Example User"
    User::email generatesBy { "example-${random.nextInt()}@example.test" }
}
```

The main code under test does not need Fiktion annotations or helper functions. Fiktion is intended to be added from tests and configured from the outside.

## Current Status

This repository currently contains the project skeleton and public API shape. The generation engine, compiler plugin behavior, and Gradle plugin behavior are not implemented yet.

The examples below describe the intended API direction represented by the current skeleton.

## Basic Usage

Generate fake data with the top-level `fake<T>()` function:

```kotlin
val user = fake<User>()
val order = fake<Order>()
```

Each call is expected to produce fresh fake data by default.

Custom generators receive a runtime context with a random instance, current type, current property, path, recursion depth, and seed when one is provided:

```kotlin
val user = fake<User> {
    User::id generatesBy {
        "user-${random.nextLong()}"
    }
}
```

## Per-Call Rules

Use the `fake<T> { ... }` block to override generation for one call.

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

Property-name matching is available in the per-call scope for root-scoped dynamic rules:

```kotlin
val user = fake<User> {
    "id" generates "user-1"
    ".*Name".toRegex() generatesBy { "generated-name" }
}
```

## Global Rules

Use `Fiktion.configure` to update the global configuration and receive a snapshot that can restore the previous configuration.

```kotlin
val snapshot = Fiktion.configure {
    // User.id: String
    rule<User, String>("id") generatesBy {
        "user-${random.nextLong()}"
    }

    // Order.id: String
    rule<Order, String>("id") generatesBy {
        "order-${random.nextLong()}"
    }

    // *.email: inferred as String
    "email" generatesBy {
        "test-${random.nextInt()}@example.test"
    }
}

try {
    val user = fake<User>()
} finally {
    snapshot.restore()
}
```

Unrestricted owner-qualified rule APIs are available from Fiktion configuration, not from `fake<T> { ... }`.

## Isolated Fiktion Instances

Create an isolated Fiktion instance when a test suite needs a named rule set instead of changing global configuration:

```kotlin
val fiktion = Fiktion {
    rule<User, String>("id") generatesBy {
        "api-user-${random.nextLong()}"
    }
}

val user = fiktion.fake<User>()
```

## Rule Targets

The global rule scope supports type, owner, property, path, and property-name targets:

```kotlin
rule<User>() generatesBy { /* any User */ }
rule<User, String>() generatesBy { /* any String property owned by User */ }
rule<User, String>("id") generates "user-1"
"id" generates "shared-id"
rule(User::id) generates "user-1"
rule(User::profile / Profile::nickname) generates "example"
```

Rule precedence is intended to be:

1. Per-call rules
2. Fiktion instance rules
3. Global rules
4. Add-on rules
5. Built-in primitive rules

When multiple rules match at the same level, rule priority will be used to decide the winner:

```kotlin
"id" generates "fallback-id" withPriority 10
rule<User, String>("id") generates "user-id" withPriority 20
```

## Optional Defaults And Nulls

Rules can opt into null or default-value generation with probabilities:

```kotlin
val user = fake<User> {
    User::nickname.autoGenerates() orNullAt 0.3
    User::status.autoGenerates() orDefaultAt 30.percent
}
```

`Double` probabilities use `0.0..1.0`. Percentage helpers are available with `percent`.

## Collections And Maps

Collection and map sizes are configured separately from element generation:

```kotlin
val catalog = fake<Catalog> {
    Catalog::items generatesEach {
        fake<Item>()
    } withSize 3

    Catalog::tags.autoGenerates() withSize (1..5)
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

    override fun install(builder: FiktionBuilder) {
        with(builder) {
            rule<Instant>() generatesBy {
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
