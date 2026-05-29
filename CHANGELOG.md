# Changelog

## v0.2.0 (Unpublished)

- Added reified `KProperty1` rule APIs backed by KType-aware overloads.
- Added built-in generation for Kotlin function types from `Function0` through `Function22`.
- Added Java add-on generation for `Runnable` and `java.util.function` functional interfaces.
- Removed `@PublishedApi` exposure from core rule/path internals.
- Made `PropertyPath.properties` internal.

## v0.1.0

Initial release of Fiktion.

This version establishes the first published API surface and artifact layout for generating fake Kotlin test data with
`fake<T>()`, compiler-generated metadata, and reusable rule configuration.

### Published Artifacts

- `dev.s7a:fiktion-core:0.1.0`
  Runtime APIs, rule DSL, built-in generators, object graph generation, and add-on support.
- `dev.s7a:fiktion-compiler-plugin:0.1.0`
  Kotlin compiler plugin that generates runtime metadata for supported Kotlin types.
- `dev.s7a.fiktion` Gradle plugin `0.1.0`
  Gradle wiring for enabling the compiler plugin on Kotlin test source sets.
- `dev.s7a:fiktion-addon-java:0.1.0`
  Add-on rules and generators for common Java/JVM standard library types.

### Initial Scope

- Runtime fake data generation through top-level and instance-based `fake<T>()`.
- Rule DSL for per-call, instance, global, add-on, and built-in configuration.
- Compiler-plugin metadata registration for supported Kotlin classes, value classes, enums, sealed hierarchies, and
  singleton objects.
- Built-in rules and generators for Kotlin primitive and standard library types.
- Collection and map generation, including automatic element/key/value generation and concrete type converters.
- Add-on infrastructure, including automatic discovery from `META-INF/fiktion/addons`.
- Java/JVM standard library add-on.
- README and Codex skill documentation for the initial API set.

### Release Notes

Fiktion is still pre-1.0. API names and compiler-generated metadata internals may change while the project incorporates
real usage feedback.
