package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.math.BigInteger

/**
 * Generates a Java big integer.
 */
public fun FakeContext.bigInteger(): BigInteger = BigInteger.valueOf(long())
