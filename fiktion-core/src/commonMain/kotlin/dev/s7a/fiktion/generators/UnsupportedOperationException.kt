package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an unsupported operation exception.
 */
public fun FakeContext.unsupportedOperationException(message: String = string()): UnsupportedOperationException =
    UnsupportedOperationException(message)
