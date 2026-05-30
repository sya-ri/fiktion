---
name: fiktion
description: Fiktion Kotlin Multiplatform fake data library guidance for using, configuring, extending, and troubleshooting fake calls in Kotlin tests. Use when Codex needs to write or review Fiktion test code, configure Gradle/compiler plugin settings, use built-in generators, install or auto-register add-ons, create a custom FiktionAddon, debug CannotGenerateException/configuration errors, or update this repository's Fiktion APIs and docs.
---

# Fiktion

Use this skill when working with Fiktion, a Kotlin Multiplatform fake data library for tests that should read like intent rather than fixture setup.

## Source Of Truth

Prefer the repository source over memory when details matter:

- Runtime API: `fiktion-core/src/commonMain/kotlin/dev/s7a/fiktion`
- Built-in generators: `fiktion-core/src/commonMain/kotlin/dev/s7a/fiktion/generators`
- Java add-on: `fiktion-addon-java/src/main/kotlin/dev/s7a/fiktion/addon/java`
- Gradle plugin: `fiktion-gradle-plugin/src/main/kotlin/dev/s7a/fiktion/gradle`
- Compiler plugin: `fiktion-compiler-plugin/src/main/kotlin/dev/s7a/fiktion/compiler`
- User docs: `README.md`

If a reference below conflicts with code, trust the code and update the reference.

## Reference Map

Read only the file needed for the task:

- Basic usage, rule precedence, target selection, collections/maps, nulls/defaults, compiler plugin, Gradle config: `references/basic-api.md`
- Built-in generator functions and generator-writing patterns: `references/generators.md`
- Java add-on usage, automatic add-on registration, and custom add-on authoring: `references/addons.md`
- Common failures, error messages, and review checklist: `references/troubleshooting.md`

## Working Conventions

- Prefer `fake<T> { ... }` per-call rules in tests unless repeated setup justifies `Fiktion { ... }` or `Fiktion.configure`.
- Prefer `Fiktion { ... }` for `kotlin.test` `@BeforeTest`, JUnit class-scoped setup, and Kotest spec-scoped defaults.
- Use `Fiktion.configure` only for process-wide defaults needed by top-level `fake<T>()` calls; pair every call with
  snapshot restoration in the matching project-level teardown hook when tests may run in parallel.
- Prefer property references in per-call rules: `User::id generates "user-1"`.
- Use typed generator configuration to adjust default generator behavior:
  `this using FiktionConfig.Int.range(-200..200)` or `User::id using FiktionConfig.String.length(12..12)`.
- Use `element`, `key`, and `value` inside collection/map fake blocks when configuring generated container
  parts: `fake<List<Map<String, Int>>> { element { key using FiktionConfig.String.length(4..4); value using FiktionConfig.Int.range(10..20) } }`.
- Container targets also work from typed rule targets, such as `property(Catalog::counts).element using FiktionConfig.Int.range(10..20)`.
- Prefer `key generatesBy { ... }` and `value generatesBy { ... }` when defining map keys or values with the target DSL.
- Prefer reified targets in global/instance/add-on rules: `property<User, String>("id")`, `type<User>()`, `typeFamily<Optional<*>>()`.
- KProperty infix rules also exist in global/instance/add-on scopes; use reified targets when ambiguity or readability is a concern.
- For value classes, target the public underlying property when available, use `name("value")` when the underlying property is private, and use `type<ValueClass>()` when replacing the whole value object.
- Prefer `generates auto` for object, collection, and map graph generation when the shape should be generated and only size or specific fields matter.
- Add explicit rules for unsupported shapes, private constructors, abstract/interface targets, or domain-specific realistic data.
- When editing this repo, keep new public generators in `generators/Xxx.kt` and add focused tests in matching generator test files.
