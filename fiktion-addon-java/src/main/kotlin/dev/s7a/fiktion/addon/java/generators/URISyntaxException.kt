package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.net.URISyntaxException

/**
 * Generates a Java URI syntax exception.
 */
public fun FakeContext.uriSyntaxException(
    input: String = string(),
    reason: String = string(),
): URISyntaxException = URISyntaxException(input, reason)
