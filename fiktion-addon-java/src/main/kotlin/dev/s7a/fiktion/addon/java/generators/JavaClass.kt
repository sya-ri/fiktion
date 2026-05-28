package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.time.Instant
import java.util.UUID

/**
 * Generates a Java class object.
 */
public fun FakeContext.javaClass(): Class<*> = oneOf(JAVA_CLASSES)

private val JAVA_CLASSES: List<Class<*>> =
    listOf(
        String::class.java,
        Boolean::class.javaObjectType,
        Byte::class.javaObjectType,
        Short::class.javaObjectType,
        Int::class.javaObjectType,
        Long::class.javaObjectType,
        Float::class.javaObjectType,
        Double::class.javaObjectType,
        BigInteger::class.java,
        BigDecimal::class.java,
        UUID::class.java,
        URI::class.java,
        Instant::class.java,
    )
