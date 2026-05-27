package dev.s7a.fiktion

/**
 * Test model used for explicit object rules.
 */
internal data class User(
    /**
     * User identifier.
     */
    val id: String,
    /**
     * User profile.
     */
    val profile: Profile = Profile(nickname = ""),
    /**
     * Optional user profile.
     */
    val optionalProfile: Profile? = null,
)
