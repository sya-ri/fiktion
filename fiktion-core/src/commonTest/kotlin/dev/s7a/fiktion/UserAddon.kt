package dev.s7a.fiktion

/**
 * Test add-on that contributes a low-precedence user rule.
 */
internal object UserAddon : FiktionAddon {
    /**
     * Stable identifier for the test add-on.
     */
    override val id: String = "test-user-addon"

    /**
     * Installs the low-precedence user rule used by add-on precedence tests.
     */
    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            type<User>() generates User(id = "addon-user")
        }
    }
}
