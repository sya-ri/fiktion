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
    private val request: GenerationRequest,
) : FakeContext by context {
    /**
     * Generates a value for the type argument at [argumentIndex] using [index] as [FakeContext.index].
     */
    public fun fake(
        index: Int,
        argumentIndex: Int = index,
    ): Any? = fake(index, argumentIndex = argumentIndex, containerPartKind = null)

    /**
     * Generates a collection element from the type argument at [argumentIndex].
     */
    public fun fakeElement(
        index: Int,
        argumentIndex: Int = 0,
    ): Any? = fake(index, argumentIndex = argumentIndex, containerPartKind = ContainerPart.Kind.Collection)

    /**
     * Generates a map key from the type argument at [argumentIndex].
     */
    public fun fakeKey(
        index: Int,
        argumentIndex: Int = 0,
    ): Any? = fake(index, argumentIndex = argumentIndex, seedIndex = index * 2, containerPartKind = ContainerPart.Kind.MapKey)

    /**
     * Generates a map value from the type argument at [argumentIndex].
     */
    public fun fakeValue(
        index: Int,
        argumentIndex: Int = 1,
    ): Any? = fake(index, argumentIndex = argumentIndex, seedIndex = index * 2 + 1, containerPartKind = ContainerPart.Kind.MapValue)

    /**
     * Generates a value for a type argument with container context.
     */
    internal fun fake(
        index: Int,
        argumentIndex: Int,
        seedIndex: Int = index,
        containerPartKind: ContainerPart.Kind?,
    ): Any? =
        generateValue(
            request =
                GenerationRequest(
                    type = argumentType(argumentIndex),
                    containerParts =
                        if (containerPartKind == null) {
                            request.containerParts
                        } else {
                            request.containerParts + ContainerPart(kind = containerPartKind, container = requestedType)
                        },
                    index = index,
                ),
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
