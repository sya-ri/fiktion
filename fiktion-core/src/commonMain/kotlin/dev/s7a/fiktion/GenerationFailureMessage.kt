@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Returns the message used when no rule or generated metadata can generate [type].
 */
internal fun missingGenerationMessage(type: KType): String =
    """
    Cannot generate $type.

    No generation rule or generated metadata was found for $type.

    Fiktion can automatically generate constructor metadata for supported Kotlin classes when the compiler plugin is enabled.
    If this type should be generated automatically, check that:
    - the Fiktion compiler plugin is applied to this source set
    - the type has a supported primary constructor
    - the type is not abstract, an interface, inner, local, or annotation class
    - the primary constructor is not private, protected, vararg, or otherwise unsupported

    To generate this type manually, add an explicit rule:
    type<$type>() generatesBy { ... }
    """.trimIndent()

/**
 * Returns the message used when sealed metadata has no selectable concrete subtypes.
 */
internal fun emptySealedMetadataMessage(type: KType): String =
    """
    Cannot generate $type.

    Generated sealed metadata for $type has no concrete subtypes.

    Check that the sealed hierarchy has concrete object or class leaves in the same compilation scope.
    To override selection manually, add an explicit rule:
    type<$type>() generatesBy { ... }
    """.trimIndent()

/**
 * Returns the message used when a generated value-like type cannot generate its underlying value.
 */
internal fun valueUnderlyingGenerationMessage(
    type: KType,
    underlyingType: KType,
): String =
    """
    Cannot generate $type.

    Generated value metadata was found for $type, but its underlying value type $underlyingType could not be generated.

    Add a rule for the underlying type:
    type<$underlyingType>() generatesBy { ... }

    Or override the value class directly:
    type<$type>() generatesBy { ... }
    """.trimIndent()

/**
 * Returns the message used when an object constructor argument cannot be generated.
 */
internal fun objectArgumentGenerationMessage(
    type: KType,
    property: FiktionObjectProperty,
): String =
    """
    Cannot generate $type.

    Failed while generating constructor argument ${property.name}: ${property.type}.

    Add a rule for the nested type or property:
    type<${property.type}>() generatesBy { ... }
    property<$type, ${property.type}>("${property.name}") generatesBy { ... }
    """.trimIndent()

/**
 * Returns the message used when a rule requests a constructor default for a property that has none.
 */
internal fun missingDefaultValueMessage(
    type: KType,
    property: FiktionObjectProperty,
): String =
    """
    Cannot generate $type.

    Rule requested the constructor default for ${property.name}: ${property.type}, but that constructor argument has no default value.

    Use `generates default` only for constructor arguments with default values, or generate an explicit value:
    property<$type, ${property.type}>("${property.name}") generatesBy { ... }
    """.trimIndent()

/**
 * Returns the message used when a rule requests a constructor default outside constructor argument generation.
 */
internal fun defaultValueWithoutConstructorArgumentMessage(type: KType): String =
    """
    Cannot generate $type.

    Rule requested a constructor default, but no constructor argument default is available for this generation request.

    Use `generates default` only for constructor arguments with default values, or generate an explicit value:
    type<$type>() generatesBy { ... }
    """.trimIndent()
