package dev.s7a.fiktion.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Gradle DSL for configuring which Kotlin source sets use the Fiktion compiler plugin.
 */
public open class FiktionExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        /**
         * Enables Fiktion for every Kotlin compilation in the project.
         */
        public val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

        /**
         * Enables Fiktion for Kotlin source sets whose default source set name ends with `Test`.
         */
        public val testEnabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

        /**
         * Source-set-specific overrides keyed by Kotlin source set name.
         */
        public val sourceSets: NamedDomainObjectContainer<FiktionSourceSetExtension> =
            objects.domainObjectContainer(FiktionSourceSetExtension::class.java) { name ->
                objects.newInstance(FiktionSourceSetExtension::class.java, name)
            }

        /**
         * Configures the Fiktion override for one Kotlin source set.
         */
        public fun sourceSet(
            name: String,
            configure: Action<in FiktionSourceSetExtension>,
        ) {
            configure.execute(sourceSets.maybeCreate(name))
        }
    }
