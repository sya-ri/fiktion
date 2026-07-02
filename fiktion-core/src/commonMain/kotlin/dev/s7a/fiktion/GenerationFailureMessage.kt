@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Returns the message used when no rule or generated metadata can generate a request.
 */
internal fun missingGenerationMessage(
    request: GenerationRequest,
    config: FiktionConfigState,
): String {
    val type = request.type
    val requestContext = request.contextLines().indentContinuationLines()
    val configurationContext = config.contextLines().indentContinuationLines()
    val ruleHints = request.ruleHintLines().indentContinuationLines()

    return """
        Cannot generate $type.

        No generation rule or generated metadata was found for $type.

        Generation request:
        $requestContext

        Current Fiktion configuration:
        $configurationContext

        Fiktion can automatically generate constructor metadata for supported Kotlin classes when the compiler plugin is enabled.
        If this type should be generated automatically, check that:
        - the Fiktion compiler plugin is applied to this source set
        - fake<T>() or Fiktion.fake<T>() is called directly from a compiler-plugin-enabled source set
        - wrapper functions around fake<T>() are not hiding the direct fake<T>() call from the compiler plugin
        - the type has a supported primary constructor
        - the type is not abstract, an interface, fun interface, inner, or annotation class
        - the primary constructor is not private, protected, vararg, or otherwise unsupported

        If this type comes from a library, install or explicitly register an add-on for that library when one exists.

        To generate this request manually, add one of:
        $ruleHints
        """.trimIndent()
}

private fun String.indentContinuationLines(): String = replace("\n", "\n        ")

/**
 * Returns request context lines for generation diagnostics.
 */
private fun GenerationRequest.contextLines(): String =
    buildList {
        add("- type: $type")
        owner?.let { owner -> add("- owner: $owner") }
        propertyName?.let { name -> add("- property: $name") }
        if (pathSegments.isNotEmpty()) {
            add("- path: ${pathSegments.joinToString(separator = ".") { segment -> segment.name }}")
        }
        if (containerParts.isNotEmpty()) {
            add("- container part: ${containerParts.joinToString(separator = " -> ") { part -> part.render() }}")
        }
        if (index != 0) {
            add("- container index: $index")
        }
    }.joinToString(separator = "\n")

/**
 * Returns configuration context lines for generation diagnostics.
 */
private fun FiktionConfigState.contextLines(): String =
    buildList {
        add("- installed add-ons: ${addons.map { addon -> addon.id }.ifEmpty { listOf("none") }.joinToString()}")
        add("- registered metadata entries: ${metadata.size}")
        add("- explicit generation rules: ${rules.size}")
        add("- explicit generator configs: ${configs.size}")
    }.joinToString(separator = "\n")

/**
 * Returns explicit rule examples for [GenerationRequest].
 */
private fun GenerationRequest.ruleHintLines(): String =
    buildList {
        add("- type<$type>() generatesBy { ... }")
        if (owner != null && propertyName != null) {
            add("- property<$owner, $type>(\"$propertyName\") generatesBy { ... }")
        }
        if (propertyName != null) {
            add("- name<$type>(\"$propertyName\") generatesBy { ... }")
        }
    }.joinToString(separator = "\n")

/**
 * Renders a container part for diagnostics.
 */
private fun ContainerPart.render(): String =
    when (kind) {
        ContainerPart.Kind.Collection -> "element of $container"
        ContainerPart.Kind.MapKey -> "key of $container"
        ContainerPart.Kind.MapValue -> "value of $container"
    }

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

/**
 * Returns the message used when a dependent property rule is selected outside object constructor generation.
 */
internal fun dependencyRuleWithoutObjectContextMessage(request: GenerationRequest): String =
    """
    Cannot generate ${request.type}.

    A dependent property rule was selected, but dependency values are available only while generating object constructor arguments.

    Use dependsOn only for direct constructor properties generated from object metadata.
    """.trimIndent()

/**
 * Returns the message used when a dependency is not a constructor property of the object currently being generated.
 */
internal fun unknownDependencyPropertyMessage(
    owner: KType,
    target: FiktionObjectProperty,
    dependency: DependentProperty,
): String =
    """
    Cannot generate $owner.

    Dependency ${dependency.name}: ${dependency.value} for ${target.name}: ${target.type} is not a direct constructor property of $owner.

    Use dependsOn only with direct properties of the same generated object.
    """.trimIndent()

/**
 * Returns the message used when a dependency has not been generated yet.
 */
internal fun dependencyOrderMessage(
    owner: KType,
    target: FiktionObjectProperty,
    dependency: DependentProperty,
): String =
    """
    Cannot generate $owner.

    Dependency ${dependency.name}: ${dependency.value} must be generated before ${target.name}: ${target.type}.

    Reorder the constructor properties, depend only on earlier properties, or generate $owner with type<$owner>() generatesBy { ... }.
    """.trimIndent()

/**
 * Returns the message used when dependency metadata does not match the declared dependency property type.
 */
internal fun dependencyTypeMismatchMessage(
    owner: KType,
    target: FiktionObjectProperty,
    dependency: DependentProperty,
    property: FiktionObjectProperty,
): String =
    """
    Cannot generate $owner.

    Dependency ${dependency.name}: ${dependency.value} for ${target.name}: ${target.type} resolved to constructor property ${property.name}: ${property.type}.

    The dependency property type does not match the generated constructor metadata.
    Check that registered metadata for $owner is up to date.
    """.trimIndent()

/**
 * Returns the message used when a dependency used a constructor default value.
 */
internal fun dependencyDefaultValueMessage(
    owner: KType,
    target: FiktionObjectProperty,
    dependency: DependentProperty,
): String =
    """
    Cannot generate $owner.

    Dependency ${dependency.name}: ${dependency.value} for ${target.name}: ${target.type} used a constructor default value.

    Fiktion cannot read constructor default values before constructing the object. Generate an explicit dependency value instead.
    """.trimIndent()
