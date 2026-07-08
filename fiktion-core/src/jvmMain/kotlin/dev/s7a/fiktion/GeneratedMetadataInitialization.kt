package dev.s7a.fiktion

import kotlin.jvm.javaObjectType
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal actual fun initializeGeneratedMetadata(type: KType) {
    type.arguments.mapNotNull { argument -> argument.type }.forEach(::initializeGeneratedMetadata)
    val kotlinClass = type.classifier as? KClass<*> ?: return
    val javaClass = kotlinClass.javaObjectType
    Class.forName(javaClass.name, true, javaClass.classLoader)
}
