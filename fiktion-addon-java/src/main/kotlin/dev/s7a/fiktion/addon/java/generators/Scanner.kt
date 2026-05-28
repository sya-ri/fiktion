package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.util.Scanner

/**
 * Generates a Java scanner backed by a random string.
 */
public fun FakeContext.scanner(): Scanner = Scanner(string())
