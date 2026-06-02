# Changelog

## Unreleased

- Added `AvoidNonPropertyRuleTargets`, an opt-in detekt rule for teams that want Fiktion rules to target explicit
  properties only.
- Added `PreferFixedNullProbability`, a detekt rule that rewrites `orNullAt 0.0`/`0.percent` to the fixed generated
  value and `orNullAt 1.0`/`100.percent` to `generates null` when the target is explicitly nullable.
- Added `generates default` for selecting constructor default values explicitly.
- Added `PreferFixedDefaultProbability`, a detekt rule that rewrites `orDefaultAt 0.0`/`0.percent` to the fixed
  generated value and `orDefaultAt 1.0`/`100.percent` to `generates default`.
- Changed nullable rules so `generates value` always returns that value unless `orNullAt` is set explicitly.
- Changed automatic nullable and defaultable generation to use `null` or constructor defaults with 50% probability while
  keeping explicit `generates value` rules fixed unless `orNullAt` or `orDefaultAt` is set.

## v0.4.0

- Added `fiktion-detekt-rules`, an optional detekt rule set for keeping Fiktion DSL usage focused and consistent.
- Added `fiktion-addon-arrow-core` with generation rules for Arrow Core `Option`, `Either`, `Ior`, `NonEmptyList`, and
  `NonEmptySet`.
- Added `FiktionConfig.Collection.uniqueElementStrategy` plus unique/minimum-size collection converter support for
  opt-in exact distinct-element generation.

## v0.3.0

- Added typed generator configuration for changing built-in and add-on generator defaults, including container part
  defaults and rules such as `fake<List<Map<String, Int>>> { element { key using FiktionConfig.String.length(4) } }`.
- Changed default collection and map sizes to include empty containers by default.
- Consolidated collection and map customization onto container targets plus `FiktionConfig.Collection.size` and
  `FiktionConfig.Map.size`.

## v0.2.4

- Fixed automatic add-on discovery for dependencies provided through Gradle test fixtures.
- Fixed compiler-plugin metadata generation for generic value classes such as `@JvmInline value class MyList<T>`.
- Added `fiktion-addon-kotlinx-datetime` with generation rules for common `kotlinx-datetime` types.
- Added coverage for exact generic type rules taking precedence over broader type-family rules independent of
  registration order.
- Reduced duplicated CI work by running the full build on Ubuntu and Apple native checks on macOS.

## v0.2.3

- Fixed value class underlying property overrides so public property references and private-property name rules can
  control generated value class values.
- Documented value class override patterns in the README and Fiktion skill guidance.

## v0.2.2

- Fixed the Gradle plugin wiring so applying `dev.s7a.fiktion` resolves the matching `fiktion-compiler-plugin`
  version instead of the initial `0.1.0` compiler plugin.

## v0.2.1

- Changed compiler-plugin metadata collection to register metadata for types reached from `fake<T>()` calls instead of
  pre-collecting every supported class in the source set.
- Added metadata collection for constructor property types and their type arguments when they are reached from a
  generated root type.
- Note that wrapper functions around `fake<T>()` are not metadata collection entry points; call `fake<T>()` or
  `Fiktion.fake<T>()` directly when compiler-generated metadata is needed.

## v0.2.0

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
