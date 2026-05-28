@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package dev.s7a.fiktion

import kotlin.test.Test
import kotlin.test.assertIs
import java.lang.Boolean as JavaBoolean
import java.lang.Byte as JavaByte
import java.lang.Character as JavaCharacter
import java.lang.Double as JavaDouble
import java.lang.Float as JavaFloat
import java.lang.Integer as JavaInteger
import java.lang.Long as JavaLong
import java.lang.Short as JavaShort
import java.lang.String as JavaString

class JvmBuiltInRulesTest {
    @Test
    fun `fake generates Java boxed and string values without configuration`() {
        assertIs<JavaBoolean>(fake<JavaBoolean>())
        assertIs<JavaByte>(fake<JavaByte>())
        assertIs<JavaShort>(fake<JavaShort>())
        assertIs<JavaInteger>(fake<JavaInteger>())
        assertIs<JavaLong>(fake<JavaLong>())
        assertIs<JavaFloat>(fake<JavaFloat>())
        assertIs<JavaDouble>(fake<JavaDouble>())
        assertIs<JavaCharacter>(fake<JavaCharacter>())
        assertIs<JavaString>(fake<JavaString>())
    }
}
