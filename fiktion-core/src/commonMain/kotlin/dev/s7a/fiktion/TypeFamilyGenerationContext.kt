package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Context for generating a value from the requested type's type arguments.
 */
public class TypeFamilyGenerationContext internal constructor(
    /**
     * Fake context for the outer value being generated.
     */
    public val context: FakeContext,
    private val requestedType: KType,
    private val config: FiktionConfigState,
) : FakeContext by context {
    /**
     * Generates a value for the type argument at [argumentIndex].
     */
    public fun fake(argumentIndex: Int): Any? = fake(argumentIndex = argumentIndex, seedIndex = argumentIndex)

    /**
     * Generates a value for the type argument at [argumentIndex] using a deterministic child seed selected by [seedIndex].
     */
    public fun fake(
        argumentIndex: Int,
        seedIndex: Int,
    ): Any? =
        generateValue(
            request = GenerationRequest(type = argumentType(argumentIndex)),
            config = config,
            seed = context.seed.childSeed(seedIndex),
            depth = context.depth + 1,
        )

    /**
     * Returns the requested type argument at [argumentIndex].
     */
    public fun argumentType(argumentIndex: Int): KType =
        requestedType.arguments.getOrNull(argumentIndex)?.type
            ?: throw CannotGenerateException("Cannot generate $requestedType because type argument $argumentIndex is unavailable.")
}
