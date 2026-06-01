package dev.s7a.fiktion

/**
 * Creates a child fake context with separate seed and public index values.
 */
public fun FakeContext.childContext(
    index: Int,
    seedIndex: Int = index,
): FakeContext =
    defaultContext().let { context ->
        DefaultFakeContext(
            seed = seed.childSeed(seedIndex),
            type = type,
            property = property,
            path = path,
            depth = depth + 1,
            index = index,
            config = context.config,
            request = context.request,
        )
    }

/**
 * Returns the internal context carrying generation configuration.
 */
private fun FakeContext.defaultContext(): DefaultFakeContext =
    when (this) {
        is DefaultFakeContext -> this
        is TypeFamilyGenerationContext -> context.defaultContext()
        else -> error("Unsupported FakeContext implementation: ${this::class}")
    }
