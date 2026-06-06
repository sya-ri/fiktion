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

Use `childContext(index, seedIndex = index)` when a public generator invokes user-supplied child generator lambdas.
This gives each generated part a deterministic child seed, increments `depth`, and exposes the intended child
`FakeContext.index` to the lambda. Use a distinct `seedIndex` when two generated parts share the same public index but
need independent random streams, such as map keys and values.

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

## Project-Local Generator Functions

Use generator functions when a project needs reusable domain-specific fake values:

```kotlin
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.string
import dev.s7a.fiktion.type

public fun FakeContext.token(length: Int = 32): Token =
    Token(value = string(length = length, charset = FiktionCharset.AlphaNumeric))
```

Register project-local generators with an isolated or global configuration when `fake<T>()` should use them
automatically:

```kotlin
val fiktion = Fiktion {
    type<Token>() generatesBy {
        token()
    }
}

val token = fiktion.fake<Token>()
```

## Type Family Generators

Use `typeFamily<T>()` for generic families where generation depends on requested type arguments:

```kotlin
typeFamily<Pair<*, *>>() generatesBy {
    Pair(
        fake(index = 0, argumentIndex = 0),
        fake(index = 1, argumentIndex = 1),
    )
}
```

Inside `TypeFamilyGenerationContext`:

- `fake(argumentIndex)` generates the requested type argument at that argument index and uses the same value as
  `FakeContext.index`.
- `fake(index, argumentIndex)` uses the requested type argument at `argumentIndex` while exposing `index` through
  `FakeContext.index`.
- `fakeElement(index)` generates collection-like elements and lets `element` target rules/config apply.
- `fakeKey(index)` and `fakeValue(index)` generate map-like keys and values with separate seeds and let `key`/`value`
  target rules/config apply.
- `argumentType(argumentIndex)` returns the requested `KType` or throws a `CannotGenerateException`.

Use valid argument indexes only. For sibling values of the same type argument, keep the `argumentIndex` fixed and vary
the context index, for example `fake(index = 1, argumentIndex = 0)`. For add-on containers, prefer `fakeElement`,
`fakeKey`, and `fakeValue` over hand-written `fake(index, argumentIndex = ...)` calls so user container target
configuration works consistently.
