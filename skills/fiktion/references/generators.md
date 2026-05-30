# Fiktion Generators

Generator lambdas run with `FakeContext` as receiver.

Useful context properties:

```kotlin
seed: Long
random: kotlin.random.Random
type: FakeType
property: FakeProperty?
path: FakePath
depth: Int
index: Int
```

Use context methods and other generators instead of ad hoc randomness when possible. Generator defaults should read
typed config keys through `config(...)`:

```kotlin
public fun FakeContext.customInt(): Int =
    int(config(FiktionConfig.Int.range))
```

## Core Built-Ins

Core has generator files under `fiktion-core/src/commonMain/kotlin/dev/s7a/fiktion/generators`.

Common functions include:

```kotlin
boolean()
byte()
byte(min, max)
byte(range)
short()
short(min, max)
short(range)
int()
int(min, max)
int(range)
long()
long(min, max)
long(range)
float()
float(min, max)
float(range)
double()
double(min, max)
double(range)
char()
char(range)
char(FiktionCharset.AlphaNumeric)
string()
string(length = 12)
string(length = 1..32)
string(length = 12, charset = FiktionCharset.LowercaseAlpha)
oneOf("a", "b", "c")
oneOf(listOf("a", "b"))
uuid()
duration()
durationUnit()
instant()
regex()
unit()
```

Unsigned variants:

```kotlin
ubyte()
ushort()
uint()
ulong()
uintRange()
ulongRange()
uintProgression()
ulongProgression()
```

Collections and tuples:

```kotlin
list(size = 3) { string() }
mutableList(size = 3) { string() }
set(size = 3) { string() }
mutableSet(size = 3) { string() }
map(size = 3, key = { string() }, value = { int() })
mutableMap(size = 3, key = { string() }, value = { int() })
sequence(size = 3) { string() }
pair(first = { string() }, second = { int() })
triple(first = { string() }, second = { int() }, third = { boolean() })
successResult { value }
failureResult { exception() }
result(failureProbability = 30.percent, value = { value })
```

Arrays:

```kotlin
intArray()
longArray()
byteArray()
shortArray()
floatArray()
doubleArray()
booleanArray()
charArray()
uintArray()
ulongArray()
ubyteArray()
ushortArray()
```

Exceptions:

```kotlin
throwable()
error()
exception()
runtimeException()
illegalStateException()
illegalArgumentException()
indexOutOfBoundsException()
concurrentModificationException()
unsupportedOperationException()
numberFormatException()
nullPointerException()
classCastException()
assertionError()
noSuchElementException()
arithmeticException()
```

## Charset Hints

Use `FiktionCharset` for string/char generation when the output format matters:

```kotlin
string(length = 8, charset = FiktionCharset.LowercaseAlpha)
char(FiktionCharset.Numeric)
```

Inspect `FiktionCharset.kt` for the current constants. Current built-ins cover lowercase ASCII letters, uppercase ASCII letters, digits, alpha combinations, and alpha-numeric combinations.

## Writing New Core Generators

Follow the existing pattern:

- Place each public generator in `generators/Xxx.kt`.
- Use `FakeContext` receiver.
- Prefer overload order with explicit min/max first, range overload second, default overload last when that is safer.
- Prefer existing generators inside new generators.
- Keep constants private to the file unless shared by multiple files.
- Add tests in `fiktion-core/src/commonTest/kotlin/dev/s7a/fiktion/generators/XxxTest.kt`.
- Add a built-in rule in `BuiltInRules.kt` only when `fake<T>()` should work without configuration.
- Add a default-generation test confirming `fake<T>()` works without custom settings for built-in types.
- Update ABI when public API changes.

Example:

```kotlin
package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

public fun FakeContext.token(length: Int = 32): Token =
    Token(value = string(length = length, charset = FiktionCharset.AlphaNumeric))
```

Built-in rule:

```kotlin
type<Token>() generatesBy {
    token()
}
```

## Type Family Generators

Use `typeFamily<T>()` for generic families where generation depends on requested type arguments:

```kotlin
typeFamily<Pair<*, *>>() generatesBy {
    Pair(
        fake(argumentIndex = 0, seedIndex = 0),
        fake(argumentIndex = 1, seedIndex = 1),
    )
}
```

Inside `TypeFamilyGenerationContext`:

- `fake(argumentIndex)` generates the requested type argument with the same index as seed index.
- `fake(argumentIndex, seedIndex)` allows deterministic sibling values.
- `argumentType(argumentIndex)` returns the requested `KType` or throws a `CannotGenerateException`.

Use distinct seed indexes for sibling values to avoid duplicated output.
