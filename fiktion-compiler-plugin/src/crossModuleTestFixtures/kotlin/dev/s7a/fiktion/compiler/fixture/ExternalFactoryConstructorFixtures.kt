package dev.s7a.fiktion.compiler.fixture

import dev.s7a.fiktion.FiktionAddon
import dev.s7a.fiktion.FiktionAddonBuilder
import dev.s7a.fiktion.FiktionRuleBuilder
import dev.s7a.fiktion.constructsBy
import dev.s7a.fiktion.generates

/**
 * Configures factory construction metadata from a different compiler module.
 */
public fun configureExternalFactoryConstructor(builder: FiktionRuleBuilder) {
    with(builder) {
        type<ExternalFactoryConstructorUser>() constructsBy ExternalFactoryConstructorUser::create
        property<ExternalFactoryConstructorUser, String>("roleName") generates "admin"
    }
}

/**
 * Add-on that contributes factory construction metadata from a different compiler module.
 */
public object ExternalFactoryConstructorAddon : FiktionAddon {
    override val id: String = "dev.s7a.fiktion.compiler.fixture.externalFactoryConstructorAddon"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<ExternalAddonFactoryConstructorUser>() constructsBy ExternalAddonFactoryConstructorUser::create
            property<ExternalAddonFactoryConstructorUser, String>("roleName") generates "admin"
        }
    }
}

/**
 * Resets the generated registrar guard in this fixture module.
 */
public fun resetExternalFactoryConstructorRegistrarGuard(value: Boolean) {
    externalFactoryConstructorRegistrarGuardField().setBoolean(null, value)
}

/**
 * Returns whether the generated registrar guard in this fixture module is set.
 */
public fun externalFactoryConstructorRegistrarGuard(): Boolean = externalFactoryConstructorRegistrarGuardField().getBoolean(null)

private fun externalFactoryConstructorRegistrarGuardField(): java.lang.reflect.Field =
    Class
        .forName("dev.s7a.fiktion.compiler.fixture.ExternalFactoryConstructorFixturesKt")
        .declaredFields
        .single { field -> field.name.contains("fiktionGeneratedMetadataRegistered") }
        .also { field -> field.isAccessible = true }

/**
 * Fixture class generated through a factory declared in another compiler module.
 */
public class ExternalFactoryConstructorUser private constructor(
    public val id: String,
    public val role: ExternalFactoryRole,
) {
    public companion object {
        public fun create(
            id: String,
            roleName: String,
        ): ExternalFactoryConstructorUser =
            ExternalFactoryConstructorUser(
                id = id,
                role = ExternalFactoryRole.valueOf(roleName.replaceFirstChar { char -> char.uppercase() }),
            )
    }
}

/**
 * Fixture class generated through an add-on factory declared in another compiler module.
 */
public class ExternalAddonFactoryConstructorUser private constructor(
    public val id: String,
    public val role: ExternalFactoryRole,
) {
    public companion object {
        public fun create(
            id: String,
            roleName: String,
        ): ExternalAddonFactoryConstructorUser =
            ExternalAddonFactoryConstructorUser(
                id = id,
                role = ExternalFactoryRole.valueOf(roleName.replaceFirstChar { char -> char.uppercase() }),
            )
    }
}

/**
 * Role converted by fixture factory methods.
 */
public enum class ExternalFactoryRole {
    Admin,
}
