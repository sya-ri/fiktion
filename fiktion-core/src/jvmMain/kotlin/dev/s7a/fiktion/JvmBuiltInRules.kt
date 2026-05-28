@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package dev.s7a.fiktion

import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.byte
import dev.s7a.fiktion.generators.char
import dev.s7a.fiktion.generators.double
import dev.s7a.fiktion.generators.float
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import dev.s7a.fiktion.generators.short
import dev.s7a.fiktion.generators.string
import java.lang.Boolean as JavaBoolean
import java.lang.Byte as JavaByte
import java.lang.Character as JavaCharacter
import java.lang.Double as JavaDouble
import java.lang.Float as JavaFloat
import java.lang.Integer as JavaInteger
import java.lang.Long as JavaLong
import java.lang.Short as JavaShort
import java.lang.String as JavaString

/**
 * Configures JVM built-ins for Java boxed and platform types.
 */
internal actual fun DefaultFiktionBuilder.configurePlatformBuiltIns() {
    typeFamily<JavaBoolean>() generatesBy {
        boolean() as JavaBoolean
    }
    typeFamily<JavaByte>() generatesBy {
        byte() as JavaByte
    }
    typeFamily<JavaShort>() generatesBy {
        short() as JavaShort
    }
    typeFamily<JavaInteger>() generatesBy {
        int() as JavaInteger
    }
    typeFamily<JavaLong>() generatesBy {
        long() as JavaLong
    }
    typeFamily<JavaFloat>() generatesBy {
        float() as JavaFloat
    }
    typeFamily<JavaDouble>() generatesBy {
        double() as JavaDouble
    }
    typeFamily<JavaCharacter>() generatesBy {
        char() as JavaCharacter
    }
    typeFamily<JavaString>() generatesBy {
        string() as JavaString
    }
}
