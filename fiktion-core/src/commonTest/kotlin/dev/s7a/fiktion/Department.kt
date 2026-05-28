package dev.s7a.fiktion

/**
 * Test model used for nested collection path rules.
 */
internal data class Department(
    /**
     * Team owned by the department.
     */
    val team: Team,
)
