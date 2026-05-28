# Fiktion Troubleshooting

## CannotGenerateException

Typical cause: no built-in, add-on, explicit rule, or generated metadata can create the requested type.

Fixes:

- Ensure the Gradle plugin is applied and enabled for the source set.
- Ensure `fiktion-core` is on the test/runtime classpath.
- Add missing add-on dependency, e.g. `fiktion-addon-java` for common JVM types.
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

## Map Rule Configuration Errors

Map rules are intentionally exclusive:

- Use `generatesEach { key to value }`, or
- Use `generatesKeys { ... }` and `generatesValues { ... }`.

Do not mix entry generation with key/value generation for the same target.

Duplicate key or duplicate value rules for the same target fail immediately.

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

Inside generators, use the provided `random`, `seed`, `index`, and existing generators instead of global randomness. For sibling values, use separate seed indexes in `TypeFamilyGenerationContext.fake(argumentIndex, seedIndex)`.

## Review Checklist For Fiktion Code

When reviewing or refactoring:

- Confirm explicit test intent is visible in `fake<T> { ... }`, not hidden in broad global state.
- Confirm global configuration is restored with `snapshot.restore()`.
- Confirm add-ons are dependencies, not manual Gradle compiler args, unless testing plugin internals.
- Confirm `generates auto` for collection/map uses size constraints when test expectations rely on size.
- Confirm custom collection/map types have converters.
- Confirm generated values for maps avoid null keys/values when target Java type forbids them.
- Confirm new public APIs have tests and ABI updates.
- Confirm tests cover default `fake<T>()` generation for new built-ins/add-on built-ins.
- Confirm docs/README and skill references are updated when API names change.
