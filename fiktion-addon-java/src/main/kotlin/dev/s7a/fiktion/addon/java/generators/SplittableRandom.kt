package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.util.SplittableRandom

/**
 * Generates a Java splittable random instance seeded from the fake context.
 */
public fun FakeContext.splittableRandom(): SplittableRandom = SplittableRandom(long())
