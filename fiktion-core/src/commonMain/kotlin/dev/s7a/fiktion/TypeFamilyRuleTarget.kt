package dev.s7a.fiktion

/**
 * Generator used by type-family rules.
 */
public typealias TypeFamilyGenerator<T> = TypeFamilyGenerationContext.() -> T

/**
 * Target selected by a type-family rule declaration.
 */
public sealed interface TypeFamilyRuleTarget<T>

/**
 * Generates values for this type-family target by invoking [generator].
 */
public infix fun <T> TypeFamilyRuleTarget<T>.generatesBy(generator: TypeFamilyGenerator<T>): GenerationSpec<T> =
    (this as DefaultTypeFamilyRuleTarget<T>).generatesBy(generator)

/**
 * Uses Fiktion's automatic generation for this type-family target.
 */
@Suppress("UNUSED_PARAMETER")
public infix fun <T> TypeFamilyRuleTarget<T>.generates(auto: Auto): GenerationSpec<T> =
    (this as DefaultTypeFamilyRuleTarget<T>).generatesAutomatically()
