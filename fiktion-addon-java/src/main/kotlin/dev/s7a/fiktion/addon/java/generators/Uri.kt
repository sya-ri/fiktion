package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.oneOf
import dev.s7a.fiktion.generators.string
import java.net.URI

/**
 * Generates a Java URI.
 */
public fun FakeContext.uri(): URI =
    URI.create(
        "${scheme()}://${host()}/${string(length = DEFAULT_PATH_SEGMENT_LENGTH, charset = FiktionCharset.LowercaseAlphaNumeric)}",
    )

private fun FakeContext.scheme(): String = oneOf(URI_SCHEMES)

private fun FakeContext.host(): String =
    "${string(length = DEFAULT_HOST_LABEL_LENGTH, charset = FiktionCharset.LowercaseAlpha)}.${oneOf(DOMAINS)}"

private val URI_SCHEMES: List<String> = listOf("http", "https")
private val DOMAINS: List<String> = listOf("example.test", "fiktion.test", "sample.invalid")

private const val DEFAULT_HOST_LABEL_LENGTH: Int = 8
private const val DEFAULT_PATH_SEGMENT_LENGTH: Int = 12
