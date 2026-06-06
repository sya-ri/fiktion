# Fiktion Troubleshooting

## CannotGenerateException

Typical cause: no built-in, add-on, explicit rule, or generated metadata can create the requested type.

Fixes:

- Ensure the Gradle plugin is applied and enabled for the source set.
- Ensure `fiktion-core` is on the test/runtime classpath.
- Call `fake<T>()` or `Fiktion.fake<T>()` directly from a compiler-plugin-enabled source set when relying on generated metadata. Wrapper functions around `fake<T>()` are not metadata collection entry points, so metadata for `T` may not be generated.
- Add missing add-on dependency, e.g. `fiktion-addon-java` for common JVM types or `fiktion-addon-kotlinx-datetime`
  for `kotlinx-datetime` types.
- For unsupported shapes such as abstract classes, interfaces, annotation classes, inner classes, private/protected
  primary constructors, or vararg constructor parameters, provide an explicit rule.
- Add an explicit rule:

```kotlin
Fiktion {
    type<UnsupportedType>() generatesBy {
        UnsupportedType(...)
    }
}
```

- For interfaces/abstract types, target the property or type and generate a concrete implementation.
- For private/protected constructors or no primary constructor, provide a factory rule.

## Add-On Type Still Fails

Check:

- Is the add-on dependency on the same source set classpath as the test?
- Is the compiler plugin enabled for the source set where `fake<T>()` is called?
- Does the add-on artifact include `META-INF/fiktion/addons`?
- Does the resource contain the object FQCN, one per line?
- If the source set has no compiler plugin, use explicit `install(AddonObject)`.

## Property Target Ambiguity

KProperty infix rules are available in per-call, global, instance, and add-on scopes:

```kotlin
Fiktion {
    User::id generates "user-1"
}
```

If matching is unclear, or when using name/regex rules, prefer reified property targets:

```kotlin
Fiktion {
    property<User, String>("id") generates "user-1"
}
```

## Name Rule Generates Null

Give the name rule a nullable value type:

```kotlin
name<String?>("nickname") generates null
```

Without this, Kotlin may infer `Nothing?`, making the rule hard to match.

## Explicit Nullable Rule Unexpectedly Returns The Generated Value

Automatic nullable generation can return `null`, but explicit nullable rules do not generate `null` unless `orNullAt`
is set:

```kotlin
type<String?>() generates "nickname"
```

This always returns `"nickname"`. Add an explicit null probability when null should be a candidate for this rule:

```kotlin
type<String?>() generates "nickname" orNullAt 30.percent
```

Use `generates null` when the intended value is always null:

```kotlin
type<String?>() generates null
```

## Explicit Rule Does Not Use A Constructor Default

Automatic generation can select constructor defaults for defaultable arguments, but explicit generated values do not use
constructor defaults unless `generates default` or `orDefaultAt` is set:

```kotlin
User::profile generates default
User::profile generates Profile(nickname = "generated") orDefaultAt 30.percent
```

`generates default` only works when the selected constructor argument has a default value. If the argument has no
default, Fiktion reports that the rule requested a constructor default for an argument with no default value. Generate
an explicit value instead:

```kotlin
User::profile generates Profile(nickname = "generated")
```

Top-level rules such as `type<Profile>() generates default` only make sense when they are selected while generating a
defaultable constructor argument. A top-level `fake<Profile>()` request has no constructor argument default to select.

## Map Key And Value Rules

Use `key generatesBy { ... }` and `value generatesBy { ... }` when defining generated map parts:

```kotlin
fake<Map<String, Int>> {
    key generatesBy { "key-$index" }
    value generatesBy { index }
}
```

## Collection Or Map Concrete Type Fails

If elements/entries are generated but materialization fails, configure a converter:

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

For add-ons, put converters in `install(builder)` so both explicit rules and `generates auto` can use them.

## Array<T>

Core cannot generally generate top-level `fake<Array<T>>()` without compiler-generated metadata/construction support. Treat generic arrays as unsupported unless a compiler-plugin path has generated metadata for a containing property/class. Prefer collections when possible or add an explicit rule.

## Determinism Issues

Use seeds:

```kotlin
fake<User>(seed = 123)
```

Inside generators, use the provided `random`, `seed`, `index`, and existing generators instead of global randomness. For
sibling values of the same type-family argument, vary `index` while keeping `argumentIndex` fixed, such as
`fake(index = 1, argumentIndex = 0)`.

## Review Checklist For Fiktion Code

When reviewing or refactoring:

- Confirm explicit test intent is visible in `fake<T> { ... }`, not hidden in broad global state.
- Confirm global configuration is restored with `snapshot.restore()`.
- Confirm add-ons are dependencies, not manual Gradle compiler args, unless testing plugin internals.
- Confirm `generates auto` for collection/map uses size constraints when test expectations rely on size.
- Confirm custom collection/map types have converters.
- Confirm generated values for maps avoid null keys/values when target Java type forbids them.
- Confirm reusable project-local generator functions are covered by the project's tests when their output matters.
