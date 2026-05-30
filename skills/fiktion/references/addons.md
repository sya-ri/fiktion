# Fiktion Add-Ons

Add-ons are reusable bundles of rules and converters. They are lower precedence than per-call, instance, and global rules, and higher than built-ins.

## Java Add-On Usage

Install dependency:

```kotlin
dependencies {
    testImplementation("dev.s7a:fiktion-addon-java:0.2.3")
}
```

With the Fiktion Gradle/compiler plugin enabled for the source set, add-ons on the compilation classpath are registered automatically before `fake<T>()` calls:

```kotlin
val instant = fake<java.time.Instant>()
val uuid = fake<java.util.UUID>()
val uri = fake<java.net.URI>()
```

When the compiler plugin is not enabled for that source set, install explicitly:

```kotlin
val fiktion = Fiktion {
    install(JavaFiktionAddon)
}
```

## Java Add-On Coverage

The Java add-on includes many common JVM types. Check `JavaFiktionAddon.kt` and `generators/*.kt` for the exact current list.

Broad categories:

- `java.time`: `Instant`, `LocalDate`, `LocalTime`, `LocalDateTime`, `Year`, `YearMonth`, `MonthDay`, `Month`, `DayOfWeek`, `OffsetDateTime`, `OffsetTime`, `ZonedDateTime`, `Clock`, `ZoneId`, `ZoneOffset`, `Duration`, `Period`
- `java.time.format` / temporal: `DateTimeFormatter`, `ChronoUnit`
- `java.util`: `UUID`, `Class`, `Properties`, `Date`, `Calendar`, `Random`, `SplittableRandom`, `BitSet`, `StringJoiner`, `Scanner`, `Locale`, `Currency`, `TimeZone`, Java collections
- `java.util.concurrent`: concurrent collections, blocking queues, `CompletableFuture`, atomics/adders/accumulators, `TimeUnit`
- `java.nio`: buffers and `Charset`
- `java.nio.file`: `Path`, `FileTime`, `PosixFilePermission`
- `java.io`: `File`, streams/readers/writers, IO exceptions
- `java.net`: `URI`, `URL`, cookies, addresses, proxy, networking exceptions
- `java.text`, `java.math`, `java.regex`, `java.logging`, `java.security`, `java.zip`, `java.sql`

Generic Java containers use `typeFamily` rules and `configureCollection` / `configureMap` converters so `fake<ArrayList<Int>>()`, `fake<Optional<User>>()`, and similar calls can generate nested values.

## Create A Custom Add-On

Basic shape:

```kotlin
public object CustomFiktionAddon : FiktionAddon {
    override val id: String = "custom"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<Token>() generatesBy {
                Token(value = string(length = 32))
            }

            typeFamily<Box<*>>() generatesBy {
                Box(value = fake(0))
            }

            typeFamily<CustomList<*>>() generatesBy {
                CustomList(List(int(config(FiktionConfig.Collection.size))) { index -> fakeElement(index) })
            }

            typeFamily<CustomMap<*, *>>() generatesBy {
                CustomMap(
                    List(int(config(FiktionConfig.Map.size))) { index -> fakeKey(index) to fakeValue(index) }.toMap(),
                )
            }

            configureCollection<CustomList<*>> { elements ->
                CustomList(elements)
            }

            configureMap<CustomMap<*, *>> { entries ->
                CustomMap(entries.toMap())
            }
        }
    }
}
```

Naming:

- Prefer `<Library>FiktionAddon`.
- Use a stable, short `id`; Java uses `java`.
- Keep generator helpers under the add-on's `generators` package.

For generic collection-like and map-like type-family generators, use `fakeElement(index)`, `fakeKey(index)`, and
`fakeValue(index)` instead of plain `fake(index, argumentIndex = ...)`. These helpers preserve `FakeContext.index`,
derive separate key/value seeds, and allow user container-target configuration such as
`type<CustomList<Int>>().element using FiktionConfig.Int.range(10..20)` and
`type<CustomMap<String, Int>>().key using FiktionConfig.String.length(4)` to apply to generated parts.

## Automatic Add-On Registration

To make a third-party add-on discoverable by dependency alone, add this resource file to the add-on artifact:

```text
META-INF/fiktion/addons
```

Each non-empty line should contain an add-on object class name:

```text
com.example.fiktion.ExampleFiktionAddon
```

The compiler plugin reads this resource from the compilation classpath and registers the listed add-ons into generated code. This avoids requiring users to write Gradle plugin `-P` options or explicit installs in normal test source sets.

## Add-On Authoring Checklist

- Add `api(project(":fiktion-core"))` or the published core dependency.
- Add generators in separate `generators/Xxx.kt` files when practical.
- Prefer generator functions on `FakeContext` for direct user use.
- Prefer `fakeElement`, `fakeKey`, and `fakeValue` in `TypeFamilyGenerationContext` for generic containers.
- Use `configureCollection` / `configureMap` for concrete collection materialization.
- For non-null Java containers such as concurrent maps/queues, filter null keys/values/elements.
- Use stable ordering/comparators for sorted collections when generated keys may not be naturally comparable.
- Test direct generator functions and `fake<T>()` default generation.
- Test generic nesting such as `fake<Optional<List<Int>>>()` or `fake<ArrayList<Optional<String>>>()` when supported.
- Add `META-INF/fiktion/addons` for automatic registration.
