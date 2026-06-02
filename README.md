# Fiktion

[![CI](https://github.com/sya-ri/fiktion/actions/workflows/ci.yml/badge.svg)](https://github.com/sya-ri/fiktion/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.s7a/fiktion-core?label=maven%20central)](https://central.sonatype.com/artifact/dev.s7a/fiktion-core)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/dev.s7a.fiktion?label=gradle%20plugin)](https://plugins.gradle.org/plugin/dev.s7a.fiktion)

Fiktion is a Kotlin Multiplatform fake data library for tests that should read like intent, not fixture setup.

It creates complete, realistic-enough object graphs from your Kotlin types, then lets each test override only the values
that matter. No annotations in production code. No builders for every model. No fixture files drifting away from the
domain.

```kotlin
@Test
fun `paid orders are shipped`() {
    val order = fake<Order> {
        Order::status generates OrderStatus.Paid
        // Other values are filled in automatically.
    }

    shippingService.ship(order)

    assertEquals(
        ShipmentStatus.Created,
        shipmentRepository.findByOrderId(order.id).status
    )
}
```

Fiktion is built for the common testing problem where the object shape matters, but most individual values do not:

```kotlin
val user = fake<User> {
    User::id generates "user-1"
}
```

You get:

- object graph generation for ordinary Kotlin classes
- scoped overrides for the fields a test actually asserts
- deterministic output with seeds
- collection, map, nullable, default-value, enum, sealed, object, class, data class, and value class support
- add-ons for external libraries, including common Java/JVM standard library types

## Status

Fiktion is pre-release. The core behavior is usable, but API names and compiler-generated metadata internals may change
before 1.0.

The repository currently contains:

- `fiktion-core`: runtime APIs and built-in generators
- `fiktion-compiler-plugin`: Kotlin compiler plugin for generated type metadata
- `fiktion-gradle-plugin`: Gradle wiring for test source sets
- `fiktion-addon-arrow-core`: rules for Arrow Core types
- `fiktion-addon-java`: rules for common Java/JVM standard library types
- `fiktion-addon-kotlinx-datetime`: rules for `kotlinx-datetime` types
- `fiktion-detekt-rules`: optional detekt rules for Fiktion DSL usage

## Install

Apply the Gradle plugin and add the runtime to your test dependencies:

```kotlin
plugins {
    kotlin("jvm") version "2.3.21"
    id("dev.s7a.fiktion") version "0.4.0"
}

dependencies {
    testImplementation("dev.s7a:fiktion-core:0.4.0")

    // Optional: Arrow Core types such as Option, Either, Ior, NonEmptyList, and NonEmptySet.
    testImplementation("dev.s7a:fiktion-addon-arrow-core:0.4.0")

    // Optional: common JVM types such as Instant, UUID, URI, and Java collections.
    testImplementation("dev.s7a:fiktion-addon-java:0.4.0")

    // Optional: kotlinx-datetime types such as LocalDate, LocalDateTime, and TimeZone.
    testImplementation("dev.s7a:fiktion-addon-kotlinx-datetime:0.4.0")

    // Optional: detekt rules that recommend equivalent, more focused Fiktion DSL forms.
    detektPlugins("dev.s7a:fiktion-detekt-rules:0.4.0")
}
```

If detekt is not configured yet, follow the
[official detekt Gradle setup](https://detekt.dev/docs/gettingstarted/gradle/) before adding `fiktion-detekt-rules`.

Fiktion is enabled for test source sets by default, including JVM `test` and Multiplatform source sets such as
`commonTest` and `jvmTest`.

## Basic Usage

Generate a value with `fake<T>()`:

```kotlin
val text = fake<String>()
val count = fake<Int>()
val user = fake<User>()
```

Use a seed when a test needs repeatable data:

```kotlin
val first = fake<User>(seed = 123)
val second = fake<User>(seed = 123)

check(first == second)
```

Override only what the test cares about:

```kotlin
val user = fake<User> {
    User::id generates "user-1"
    User::displayName generates "Test User"
}
```

Rules inside `fake<T> { ... }` apply to that generated object graph only, so one test does not accidentally configure
another model.

## Nested Properties

Nested objects can be configured inline:

```kotlin
val user = fake<User> {
    User::profile {
        Profile::nickname generates "example"
    }
}
```

You can also target a nested path directly:

```kotlin
val user = fake<User> {
    (User::profile / Profile::nickname) generates "example"
}
```

Name rules are useful when the same convention appears across several types:

```kotlin
val user = fake<User> {
    name("id") generates "user-1"
    name(".*Name".toRegex()) generatesBy { "generated-name" }
}
```

## Collections And Maps

Collections can generate each element automatically:

```kotlin
val catalog = fake<Catalog> {
    Catalog::items {
        this using FiktionConfig.Collection.size(3)
    }
}
```

Or you can provide element rules:

```kotlin
val catalog = fake<Catalog> {
    Catalog::tags {
        this using FiktionConfig.Collection.size(1..5)
        element generatesBy { string(length = 8) }
    }
}
```

Maps support key and value targets:

```kotlin
val index = fake<SearchIndex> {
    SearchIndex::entries {
        key generatesBy { string(length = 8) }
        value generatesBy { fake<Entry>() }
    }

    SearchIndex::aliases generatesOneOf listOf("primary", "secondary")
}
```

## Nulls And Defaults

Nullable values can be generated by probability:

```kotlin
val user = fake<User> {
    User::nickname generates "nickname" orNullAt 0.3
}
```

Constructor defaults can also be selected when Fiktion has metadata for the type:

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

## Reusable Configuration

Use `Fiktion { ... }` when a test suite needs a local generator configuration:

```kotlin
val fiktion = Fiktion {
    type<User>() generatesBy {
        User(id = "api-user-${random.nextLong()}")
    }
}

val user = fiktion.fake<User>()
```

Use `Fiktion.configure` for process-wide test configuration. It returns a snapshot so the previous configuration can be
restored:

```kotlin
val snapshot = Fiktion.configure {
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

`Fiktion.configure` is useful when the same defaults should apply to top-level `fake<T>()` calls across a test project
or framework-managed test context:

```kotlin
val snapshot = Fiktion.configure {
    name<String>("email") generatesBy {
        "user-${random.nextLong()}@example.test"
    }
    name<String>("id") generatesBy {
        "id-${random.nextLong()}"
    }
}
```

Because the configuration is global, prefer `fake<T> { ... }` or `Fiktion { ... }` for rules that only one test needs.
When global configuration is installed by a test hook, restore the snapshot in the matching teardown hook. `restore()`
returns `false` if another `Fiktion.configure` call has installed a newer global configuration; use `restore(force =
true)` only from cleanup code that owns the whole test process or project-level configuration.

Rule precedence is:

1. Per-call rules
2. Fiktion instance rules
3. Global rules
4. Add-on rules
5. Built-in rules

Within the same precedence level, more specific targets win before registration order. When two matching rules have the
same specificity, the later registration wins.

## Generator Defaults

Use typed generator configuration when you want to keep Fiktion's default generators but adjust their ranges, sizes, or
formats:

```kotlin
val fiktion = Fiktion {
    this using FiktionConfig.Int.range(-200..200)
    this using FiktionConfig.String.length(8)
    this using FiktionConfig.Collection.size(3)
}

val users = fiktion.fake<List<User>>()
```

Per-call configuration is scoped to the generated root type:

```kotlin
val names = fake<List<String>> {
    this using FiktionConfig.Collection.size(5)
}
```

Sets use normal set semantics by default, so duplicate generated elements can collapse and make the final set smaller
than `FiktionConfig.Collection.size`. Use `UniqueElementStrategy.Exact` when a set must contain the configured number of
distinct values:

```kotlin
val labels = fake<Set<String>> {
    this using FiktionConfig.Collection.size(3)
    this using FiktionConfig.Collection.uniqueElementStrategy(UniqueElementStrategy.Exact(maxAttemptsPerElement = 16))
}
```

Exact set generation retries candidate values up to `size * maxAttemptsPerElement`. If Fiktion cannot produce enough
distinct values within that bound, generation fails instead of silently returning a smaller set.

Custom collection converters can opt into the same distinct-element generation:

```kotlin
val fiktion = Fiktion {
    configureCollection<CustomSet<*>>(unique = true) { elements ->
        CustomSet(elements)
    }
}
```

Container target configuration narrows defaults to values generated below collection and map roots:

```kotlin
val counts = fake<List<Int>> {
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
```

Use `key` and `value` targets when defining map key or value generation, including nested configuration blocks.

Property configuration narrows a generator default to one property:

```kotlin
val user = fake<User> {
    User::id using FiktionConfig.String.length(12)
}
```

Configuration keys are grouped under `FiktionConfig`, with add-on specific keys under add-on config objects such as
`JavaFiktionConfig` and `KotlinxDatetimeFiktionConfig`.

## Detekt Rules

Fiktion often has several equivalent DSL forms. `fiktion-detekt-rules` helps teams keep those choices consistent by
recommending the narrower or more direct form when two forms express the same intent. This is useful for shared test
code because the rule feedback appears during normal linting, before the style spreads through fixtures and helpers.

Add it as a detekt plugin dependency. If detekt is not configured in the project yet, follow the
[official detekt Gradle setup](https://detekt.dev/docs/gettingstarted/gradle/) first. The rules are published under the
`fiktion` rule set.

```kotlin
dependencies {
    detektPlugins("dev.s7a:fiktion-detekt-rules:0.4.0")
}
```

See [fiktion-detekt-rules](fiktion-detekt-rules/README.md) for rule details.

## Test Framework Integration

With `kotlin.test`, prefer an isolated `Fiktion { ... }` instance from `@BeforeTest` when each test should start from
the same defaults. Use `Fiktion.configure` with `@BeforeTest` / `@AfterTest` only when the test intentionally needs
top-level `fake<T>()` calls to see global rules:

```kotlin
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import kotlin.test.BeforeTest
import kotlin.test.Test

class UserServiceTest {
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
        val user = fiktion.fake<User>()

        // test body
    }
}
```

With JUnit 5, `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` only shares one test class instance across methods in
that class. Prefer an isolated instance for class-scoped defaults:

```kotlin
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generators.string
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

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
        val user = fiktion.fake<User>()

        // test body
    }
}
```

Use `Fiktion.configure` when the rules intentionally need to affect global `fake<T>()` calls, for example project-wide
test helpers or framework hooks that do not receive a `Fiktion` instance. Since this mutates process-wide state, keep it
at project-level setup when tests may run in parallel.

For JUnit 5, put process-wide defaults in an extension and store the snapshot in the root extension store:

```kotlin
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionSnapshot
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

class FiktionExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        context.root
            .getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent(FiktionResource::class.java) {
                FiktionResource()
            }
    }

    private class FiktionResource : AutoCloseable {
        private val snapshot: FiktionSnapshot =
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

With Kotest, prefer an isolated instance for spec-scoped defaults. Kotest specs are `SingleInstance` by default, so a
property initialized in the spec body is shared by tests in that spec:

The same pattern works with other Kotest spec styles such as `FunSpec`, `DescribeSpec`, `FreeSpec`, `ShouldSpec`, and
`BehaviorSpec`; keep the `Fiktion { ... }` instance at the spec scope and call `fiktion.fake<T>()` from tests.

```kotlin
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import io.kotest.core.spec.style.StringSpec

class UserRepositorySpec : StringSpec({
    val fiktion = Fiktion {
        name<String>("email") generatesBy {
            "user-${random.nextLong()}@example.test"
        }
    }

    "finds user by email" {
        val user = fiktion.fake<User>()

        // test body
    }
})
```

Kotest project configuration is useful only when global defaults should affect top-level `fake<T>()` across the whole
test project:

```kotlin
package io.kotest.provided

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionSnapshot
import io.kotest.core.config.AbstractProjectConfig

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

Configuration scopes support type, property, path, and name targets:

```kotlin
type<User>() generatesBy { /* any User */ }
property<User, String>() generatesBy { /* any String property owned by User */ }
property<User, String>("id") generates "user-1"
name<String>("id") generates "shared-id"
name("email") generatesBy { "test-${random.nextInt()}@example.test" }
property(User::profile / Profile::nickname) generates "example"
User::id generates "user-1"
```

Property references such as `User::id generates "user-1"` are the concise form for owner-specific property rules.
`property<User, String>("id")` is the equivalent explicit form when the owner and value type should be spelled out.

## Compiler Plugin

The compiler plugin generates runtime metadata for Kotlin types in enabled source sets. This is what lets Fiktion create
objects without annotations.

Supported shapes include:

- regular classes and data classes with supported primary constructors
- value classes with one constructor value
- enum classes
- sealed classes and sealed interfaces
- singleton objects and companion objects

Shapes that should be configured explicitly are skipped:

- abstract classes and interfaces
- fun interfaces and annotation classes
- inner classes and local classes
- classes without a primary constructor
- private or protected primary constructors
- vararg or otherwise unsupported constructor parameters

Value class overrides depend on what the test wants to control. For a public underlying property, target that property:

```kotlin
@JvmInline
value class UserId(val value: String)

val userId = fake<UserId> {
    UserId::value generates "user-1"
}
```

For a private underlying property, use a name rule because Kotlin code outside the class cannot reference the property:

```kotlin
@JvmInline
value class UserId(private val value: String)

val userId = fake<UserId> {
    name("value") generates "user-1"
}
```

When the whole value object should be fixed, prefer a type rule in shared configuration:

```kotlin
val fiktion = Fiktion {
    type<UserId>() generates UserId("user-1")
}
```

Skipped types can still be generated with explicit rules:

```kotlin
val fiktion = Fiktion {
    type<PrivateUser>() generates PrivateUser.create("user-1")
}
```

## Gradle Configuration

Test source sets are enabled by default. Main/runtime source sets are opt-in:

```kotlin
fiktion {
    sourceSet("commonMain") {
        enabled.set(true)
    }
}
```

Project-wide controls are also available:

```kotlin
fiktion {
    // Enable every Kotlin source set.
    enabled.set(true)

    // Disable the default test-source-set behavior.
    testEnabled.set(false)

    sourceSet("jvmTest") {
        enabled.set(true)
    }
}
```

## Java Add-On

Add `fiktion-addon-java` when tests need common JVM types such as `java.time`, `java.util`, `java.net`, `java.nio`,
`java.sql`, or `java.util.concurrent` types:

```kotlin
dependencies {
    testImplementation("dev.s7a:fiktion-addon-java:0.4.0")
}
```

With the Gradle plugin enabled, add-ons on the compilation classpath are registered automatically before `fake<T>()`
calls:

```kotlin
import java.time.Instant
import java.util.UUID

val instant = fake<Instant>()
val uuid = fake<UUID>()
```

Add-ons can still be installed explicitly when the compiler plugin is not enabled for that source set:

```kotlin
val fiktion = Fiktion {
    install(JavaFiktionAddon)
}
```

## Arrow Core Add-On

Add `fiktion-addon-arrow-core` when tests need Arrow Core types such as `Option`, `Either`, `Ior`, `NonEmptyList`, or
`NonEmptySet`:

```kotlin
dependencies {
    testImplementation("dev.s7a:fiktion-addon-arrow-core:0.4.0")
}
```

With the Gradle plugin enabled, the add-on is registered automatically before `fake<T>()` calls:

```kotlin
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option

val option = fake<Option<Int>>()
val either = fake<Either<String, Int>>()
val items = fake<NonEmptyList<String>>()
```

`NonEmptyList` and `NonEmptySet` use collection converters with a minimum size of `1`. `NonEmptySet` also follows normal
set semantics by default: duplicate generated values collapse, so the final set size can be smaller than
`FiktionConfig.Collection.size`. Use `FiktionConfig.Collection.uniqueElementStrategy` when exact distinct sizes are
required.

It can also be installed explicitly:

```kotlin
val fiktion = Fiktion {
    install(ArrowCoreFiktionAddon)
}
```

## kotlinx-datetime Add-On

Add `fiktion-addon-kotlinx-datetime` when tests need `kotlinx-datetime` types such as `Instant`, `LocalDate`,
`LocalTime`, `LocalDateTime`, `TimeZone`, `UtcOffset`, `DatePeriod`, or `DateTimePeriod`:

```kotlin
dependencies {
    testImplementation("dev.s7a:fiktion-addon-kotlinx-datetime:0.4.0")
}
```

With the Gradle plugin enabled, the add-on is registered automatically before `fake<T>()` calls:

```kotlin
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

val date = fake<LocalDate>()
val timeZone = fake<TimeZone>()
```

It can also be installed explicitly:

```kotlin
val fiktion = Fiktion {
    install(KotlinxDatetimeFiktionAddon)
}
```

## Custom Add-Ons

Add-ons are reusable bundles of rules:

```kotlin
public object CustomFiktionAddon : FiktionAddon {
    override val id: String = "custom"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<Token>() generatesBy {
                Token(value = string(length = 32))
            }

            typeFamily<CustomList<*>>() generatesBy {
                CustomList(List(int(config(FiktionConfig.Collection.size))) { index -> fakeElement(index) })
            }
        }
    }
}
```

When writing generic collection-like or map-like add-on generators, prefer `fakeElement(index)`, `fakeKey(index)`, and
`fakeValue(index)` in `TypeFamilyGenerationContext`. These helpers keep `element`, `key`, and `value` target
configuration working for users of the add-on.

To make a third-party add-on auto-registerable, include a resource file named `META-INF/fiktion/addons` in the add-on
artifact. Each non-empty line should contain one add-on object class name:

```text
com.example.fiktion.ExampleFiktionAddon
```

Installed add-ons sit below explicit per-call, instance, and global rules in precedence.

## Realistic Data

Fiktion core focuses on object graph generation, rule resolution, and Kotlin metadata. Strict domain data such as names,
email addresses, postal addresses, or localized text can come from custom rules, add-ons, or libraries such as Datafaker.

```kotlin
val faker = Faker()

val user = fake<User> {
    User::email generatesBy {
        faker.internet().emailAddress()
    }
}
```

## Development

Local `./gradlew build` skips browser test execution unless `-Pfiktion.enableBrowserTests=true` is provided. CI enables
browser tests and installs Chrome before running Gradle.
