package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a boolean.
 */
public fun FakeContext.boolean(): Boolean = random.nextBoolean()
