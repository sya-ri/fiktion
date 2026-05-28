package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.security.MessageDigest

/**
 * Generates a Java message digest.
 */
public fun FakeContext.messageDigest(): MessageDigest = MessageDigest.getInstance(oneOf(MESSAGE_DIGEST_ALGORITHMS))

private val MESSAGE_DIGEST_ALGORITHMS: List<String> =
    listOf(
        "MD5",
        "SHA-1",
        "SHA-256",
        "SHA-384",
        "SHA-512",
    )
