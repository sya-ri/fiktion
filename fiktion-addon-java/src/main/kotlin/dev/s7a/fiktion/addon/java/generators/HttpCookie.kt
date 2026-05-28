package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.string
import java.net.HttpCookie

/**
 * Generates a Java HTTP cookie.
 */
public fun FakeContext.httpCookie(): HttpCookie =
    HttpCookie(
        string(length = DEFAULT_COOKIE_NAME_LENGTH, charset = FiktionCharset.LowercaseAlphaNumeric),
        string(),
    )

private const val DEFAULT_COOKIE_NAME_LENGTH: Int = 12
