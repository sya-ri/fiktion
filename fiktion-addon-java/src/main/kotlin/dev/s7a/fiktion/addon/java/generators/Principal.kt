package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.string
import java.security.Principal

/**
 * Generates a Java principal with a deterministic name.
 */
public fun FakeContext.principal(): Principal {
    val name = string(length = DEFAULT_PRINCIPAL_NAME_LENGTH, charset = FiktionCharset.LowercaseAlphaNumeric)
    return Principal { name }
}

private const val DEFAULT_PRINCIPAL_NAME_LENGTH: Int = 12
