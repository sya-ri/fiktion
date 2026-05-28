# fiktion

Fiktion is a Kotlin Multiplatform fake data library for tests.

It is designed for tests that need complete object graphs quickly, while still letting the test pin the values that matter.

```kotlin
// Generate a complete fake value.
val anyUser = fake<User>()

// Override only the value this test cares about.
val user = fake<User> {
    User::id generates "user-1"
}
```

The code under test does not need Fiktion annotations or helper functions. Fiktion is configured from the test side, with generated metadata supplied by the compiler plugin.

## Current Status

This repository contains the core runtime, compiler plugin, and Gradle plugin skeleton for Fiktion.

Implemented pieces include:

- primitive built-in generation for common scalar types
- per-call, isolated instance, global, and add-on rule precedence
- deterministic seeds and rule-level seed overrides
- object graph generation from registered or compiler-generated metadata
- constructor default and nullable probabilities
- collection and map generation rules
- enum, sealed, object, class, data class, and value class metadata generation
- Gradle source-set controls for enabling the compiler plugin

The project is still pre-release. API names and generated metadata internals may change before 1.0.

Local `./gradlew build` skips browser test execution unless `-Pfiktion.enableBrowserTests=true` is provided. CI enables browser tests and installs Chrome before running Gradle.

## Basic Usage

Generate fake data with the top-level `fake<T>()` function:

```kotlin
val text = fake<String>()
val count = fake<Int>()
val user = fake<User>()
```

Each call produces fresh fake data by default. Pass a seed when a test needs deterministic values:

```kotlin
val first = fake<User>(seed = 123)
val second = fake<User>(seed = 123)

check(first == second)
```

## Per-Call Rules

Use the `fake<T> { ... }` block to override generation for one call:

```kotlin
val user = fake<User> {
    User::id generates "user-1"
    User::displayName generates "Test User"
}
```

Per-call rules are scoped to the generated root type. This prevents `fake<User> { ... }` from accidentally configuring unrelated models.

Nested values can be configured directly:

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

Property-name matching is available inside the generated graph:

```kotlin
val user = fake<User> {
    name("id") generates "user-1"
    name(".*Name".toRegex()) generatesBy { "generated-name" }
}
```

## Global Rules

Use `Fiktion.configure` to update the global configuration and receive a snapshot that can restore the previous configuration.

```kotlin
val snapshot = Fiktion.configure {
    type<User>() generatesBy {
        User(id = "user-${random.nextLong()}")
    }

    // Applies to String properties named "email".
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

Use `snapshot.restore(force = true)` only when the previous configuration must be restored even if another configuration was installed after this snapshot.

## Isolated Fiktion Instances

Create an isolated Fiktion instance when a test suite needs a local rule set instead of changing global configuration:

```kotlin
val fiktion = Fiktion {
    type<User>() generatesBy {
        User(id = "api-user-${random.nextLong()}")
    }
}

val user = fiktion.fake<User>()
```

## Rule Targets

Configuration scopes support type, owner-qualified property, path, and name targets:

```kotlin
type<User>() generatesBy { /* any User */ }
property<User, String>() generatesBy { /* any String property owned by User */ }
property<User, String>("id") generates "user-1"
name<String>("id") generates "shared-id"
name("email") generatesBy { "test-${random.nextInt()}@example.test" }
property(User::profile / Profile::nickname) generates "example"
```

Bare property-reference rules such as `User::id generates "user-1"` are intentionally unavailable in global and isolated configuration scopes because Kotlin common code cannot recover the owner and value type from the reference alone. Use `property<User, String>("id")` there.

Rule precedence is:

1. Per-call rules
2. Fiktion instance rules
3. Global rules
4. Add-on rules
5. Built-in primitive rules

Within the same precedence level, more specific targets win before registration order. When two matching rules have the same specificity, the later registration wins.

## Defaults And Nulls

Rules can opt into null generation with probabilities when the requested value type is nullable:

```kotlin
val user = fake<User> {
    User::nickname generates "nickname" orNullAt 0.3
}
```

Constructor defaults can also be selected by probability when metadata says the property has a default:

```kotlin
val user = fake<User> {
    User::profile generates Profile(nickname = "generated") orDefaultAt 30.percent
}
```

`Double` probabilities use `0.0..1.0`. Percentage helpers are available with `percent`.

When a name rule intentionally generates `null`, declare the value type explicitly:

```kotlin
name<String?>("nickname") generates null
name<String?>("nickname") generatesBy { null }
```

## Collections And Maps

Collection and map sizes are configured separately from element generation:

```kotlin
val catalog = fake<Catalog> {
    Catalog::items generates auto withSize 3

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

    SearchIndex::aliases generatesOneOf listOf("primary", "secondary")
}
```

## Compiler Plugin

The compiler plugin generates runtime metadata for supported Kotlin types in enabled source sets. It currently supports:

- regular classes and data classes with supported primary constructors
- value classes with one constructor value
- enum classes
- sealed classes and sealed interfaces
- singleton objects and companion objects

It intentionally skips shapes that should be configured explicitly:

- abstract classes and interfaces
- fun interfaces and annotation classes
- inner classes and local classes
- classes without a primary constructor
- private or protected primary constructors
- vararg or otherwise unsupported constructor parameters

Skipped types can still be generated with explicit rules:

```kotlin
val fiktion = Fiktion {
    type<PrivateUser>() generates PrivateUser.create("user-1")
}
```

## Gradle

Apply the Gradle plugin to wire the compiler plugin into Kotlin compilations:

```kotlin
plugins {
    id("dev.s7a.fiktion")
}
```

Fiktion is enabled for test source sets by default. Main/runtime source sets are opt-in:

```kotlin
fiktion {
    sourceSet("commonMain") {
        enabled = true
    }
}
```

Project-wide controls are also available:

```kotlin
fiktion {
    // Enable every Kotlin source set.
    enabled = true

    // Disable the default test-source-set behavior.
    testEnabled = false

    sourceSet("jvmTest") {
        enabled = true
    }
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

Custom add-ons can be implemented with `FiktionAddon`:

```kotlin
public object CustomFiktionAddon : FiktionAddon {
    override val id: String = "custom"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<...>() generatesBy {
                // Add-on-provided generation rule.
                TODO()
            }
        }
    }
}
```

Installed add-ons sit below explicit per-call, instance, and global rules in precedence.
