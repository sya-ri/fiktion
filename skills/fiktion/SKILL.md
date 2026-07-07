---
name: fiktion
description: Fiktion Kotlin Multiplatform fake data library guidance for library users who need to install Fiktion, generate fake values in Kotlin tests, configure rules, use built-in generators, install add-ons, create project-local add-ons, enable optional detekt rules, or troubleshoot CannotGenerateException and configuration issues.
---

# Fiktion

Use this skill when writing or reviewing Kotlin test code that uses Fiktion as a library. Keep answers user-facing:
show Gradle setup, imports, DSL examples, add-on usage, detekt configuration, and troubleshooting steps that an
application or library team can apply in their own project.

Do not assume the reader is contributing to the Fiktion repository. Repository-internal implementation, release, rule
authoring, or public API maintenance work belongs in `.codex/skills`, not this public skill.

## What Fiktion Does

Fiktion generates fake test data from Kotlin type information and user rules:

```kotlin
val user = fake<User> {
    withSeed 123
    User::id generates "user-1"
    User::displayName generatesBy { "User ${int(1, 999)}" }
}
```

Prefer narrow, test-local rules first. Use an isolated `Fiktion { ... }` instance when a test class or suite needs
shared defaults. Use `Fiktion.configure { ... }` only for process-wide top-level `fake<T>()` defaults, and restore the
returned snapshot in teardown.

## Reference Map

Read only the file needed for the user's task:

- Basic usage, rule precedence, target selection, collections/maps, nulls/defaults, compiler plugin, Gradle config: `references/basic-api.md`
- Built-in generator functions, project-local generator patterns, and realistic domain data: `references/generators.md`
- Add-on usage, automatic add-on registration, and custom add-on authoring: `references/addons.md`
- Optional detekt setup and available Fiktion rules: `references/detekt-rules.md`
- Common failures, error messages, and review checklist: `references/troubleshooting.md`

## Working Conventions

- Prefer `fake<T> { ... }` per-call rules in tests unless repeated setup justifies `Fiktion { ... }` or `Fiktion.configure`.
- Prefer `Fiktion { ... }` for `kotlin.test` `@BeforeTest`, JUnit class-scoped setup, and Kotest spec-scoped defaults.
- Use `Fiktion.configure` only for process-wide defaults needed by top-level `fake<T>()` calls; pair every call with
  snapshot restoration in the matching project-level teardown hook when tests may run in parallel.
- Prefer property references in per-call rules: `User::id generates "user-1"`.
- Use typed generator configuration to adjust default generator behavior:
  `this using FiktionConfig.Int.range(-200..200)` or `User::id using FiktionConfig.String.length(12..12)`.
- Use candidate-range config helpers to shape generated values: `range`, `length`, and `size` reset the candidate range;
  `min`/`max` remove candidates outside one edge; `excluding`, `excludingLengths`, `excludingSizes`, `excludingBounds`,
  `excludingSteps`, `excludingEpochSeconds`, and `excludingNanoseconds` are ordinary config keys that replace the
  excluded ranges for the candidate set.
- In custom generators and add-ons, sample from composed candidates with `FiktionConfig.Int.range()`,
  `FiktionConfig.String.length()`, or `FiktionConfig.Collection.size()`.
- When several configs with the same scope match, they apply from lower precedence to higher precedence and, within the
  same scope, in declaration order. For example, `range(10..20)` then `min(15)` becomes `15..20`, while `min(15)` then
  `range(10..20)` becomes `10..20` because `range` resets candidates.
- Prefer fixed config values over equal-bound config ranges:
  `FiktionConfig.Collection.size(2)` instead of `FiktionConfig.Collection.size(2..2)`.
- Use `element`, `key`, and `value` inside collection/map fake blocks when configuring generated container
  parts: `fake<List<Map<String, Int>>> { element { key using FiktionConfig.String.length(4..4); value using FiktionConfig.Int.range(10..20) } }`.
- Container targets also work from typed rule targets, such as `property(Catalog::counts).element using FiktionConfig.Int.range(10..20)`.
- Prefer `key generatesBy { ... }` and `value generatesBy { ... }` when defining map keys or values with the target DSL.
- Prefer reified targets in global/instance/add-on rules: `property<User, String>("id")`, `type<User>()`, `typeFamily<Optional<*>>()`.
- KProperty infix rules also exist in global/instance/add-on scopes; use reified targets when ambiguity or readability is a concern.
- For value classes, target the public underlying property when available, use `name("value")` when the underlying property is private, and use `type<ValueClass>()` when replacing the whole value object.
- Prefer `generates auto` for object, collection, and map graph generation when the shape should be generated and only size or specific fields matter.
- Add explicit rules for unsupported shapes, private constructors, abstract/interface targets, or domain-specific realistic data.
