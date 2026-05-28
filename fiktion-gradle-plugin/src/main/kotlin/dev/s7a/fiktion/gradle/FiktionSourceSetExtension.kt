package dev.s7a.fiktion.gradle

import org.gradle.api.Named
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Source-set-specific Fiktion compiler plugin configuration.
 */
public open class FiktionSourceSetExtension
    @Inject
    constructor(
        private val name: String,
        objects: ObjectFactory,
    ) : Named {
        /**
         * Enables or disables Fiktion for this Kotlin source set.
         */
        public val enabled: Property<Boolean> = objects.property(Boolean::class.java)

        override fun getName(): String = name
    }
