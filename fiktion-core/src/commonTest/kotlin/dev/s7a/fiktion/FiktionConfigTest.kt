package dev.s7a.fiktion

import dev.s7a.fiktion.generators.FiktionCharset
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class FiktionConfigTest {
    @Test
    fun `instance config changes built-in generator defaults`() {
        val fiktion =
            Fiktion {
                this using FiktionConfig.Int.range(-200..200)
            }

        assertTrue(fiktion.fake<Int>(seed = 1) in -200..200)
    }

    @Test
    fun `per-call root config only accepts matching config scope`() {
        val value =
            fake<Int>(seed = 1) {
                this using FiktionConfig.Int.range(10..20)
            }

        assertTrue(value in 10..20)
    }

    @Test
    fun `per-call root config accepts supertype config scope`() {
        val value =
            fake<List<Int>>(seed = 1) {
                this using FiktionConfig.Collection.size(5)
            }

        assertEquals(5, value.size)
    }

    @Test
    fun `range config accepts a fixed value`() {
        val value =
            fake<List<Int>>(seed = 1) {
                this using FiktionConfig.Collection.size(2)
                element using FiktionConfig.Int.range(42)
            }

        assertEquals(listOf(42, 42), value)
    }

    @Test
    fun `per-call collection element config applies to generated elements`() {
        val value =
            fake<List<Int>>(seed = 1) {
                this using FiktionConfig.Collection.size(5)
                element using FiktionConfig.Int.range(10..20)
            }

        assertEquals(5, value.size)
        assertTrue(value.all { element -> element in 10..20 })
    }

    @Test
    fun `per-call nested collection element config applies to generated nested elements`() {
        val value =
            fake<List<List<Int>>>(seed = 1) {
                this using FiktionConfig.Collection.size(2)
                element using FiktionConfig.Collection.size(3)
                element.element using FiktionConfig.Int.range(10..20)
            }

        assertEquals(2, value.size)
        assertTrue(value.all { element -> element.size == 3 })
        assertTrue(value.flatten().all { element -> element in 10..20 })
    }

    @Test
    fun `per-call map key and value config applies to generated map parts`() {
        val value =
            fake<Map<String, Int>>(seed = 1) {
                this using FiktionConfig.Map.size(3)
                key using FiktionConfig.String.length(4)
                value using FiktionConfig.Int.range(10..20)
            }

        assertEquals(3, value.size)
        assertTrue(value.keys.all { key -> key.length == 4 })
        assertTrue(value.values.all { value -> value in 10..20 })
    }

    @Test
    fun `per-call map key and value targets can generate map parts`() {
        val value =
            fake<Map<String, Int>>(seed = 1) {
                this using FiktionConfig.Map.size(3)
                key generatesBy { "key-$index" }
                this.value generatesBy { index }
            }

        assertEquals(
            mapOf(
                "key-0" to 0,
                "key-1" to 1,
                "key-2" to 2,
            ),
            value,
        )
    }

    @Test
    fun `per-call nested map value collection element config applies to generated nested elements`() {
        val value =
            fake<Map<String, List<Int>>>(seed = 1) {
                this using FiktionConfig.Map.size(2)
                value using FiktionConfig.Collection.size(3)
                value.element using FiktionConfig.Int.range(10..20)
            }

        assertEquals(2, value.size)
        assertTrue(value.values.all { element -> element.size == 3 })
        assertTrue(value.values.flatten().all { element -> element in 10..20 })
    }

    @Test
    fun `per-call nested map key and value element configs stay independent`() {
        val value =
            fake<Map<List<Int>, List<Int>>>(seed = 1) {
                this using FiktionConfig.Map.size(3)
                key using FiktionConfig.Collection.size(2)
                key.element using FiktionConfig.Int.range(10..20)
                this.value using FiktionConfig.Collection.size(4)
                this.value.element using FiktionConfig.Int.range(100..200)
            }

        assertTrue(value.keys.all { key -> key.size == 2 })
        assertTrue(value.keys.flatten().all { element -> element in 10..20 })
        assertTrue(value.values.all { value -> value.size == 4 })
        assertTrue(value.values.flatten().all { element -> element in 100..200 })
    }

    @Test
    fun `per-call container target blocks can configure nested map parts`() {
        val value =
            fake<List<Map<String, Int>>>(seed = 1) {
                this using FiktionConfig.Collection.size(2)
                element {
                    this using FiktionConfig.Map.size(3)
                    key using FiktionConfig.String.length(4)
                    value using FiktionConfig.Int.range(10..20)
                }
            }

        assertEquals(2, value.size)
        assertTrue(value.all { map -> map.size == 3 })
        assertTrue(value.flatMap { map -> map.keys }.all { key -> key.length == 4 })
        assertTrue(value.flatMap { map -> map.values }.all { value -> value in 10..20 })
    }

    @Test
    fun `per-call map key and value target blocks can configure nested container parts`() {
        val value =
            fake<Map<List<Int>, List<Int>>>(seed = 1) {
                key {
                    this using FiktionConfig.Collection.size(2)
                    element using FiktionConfig.Int.range(10..20)
                }
                value {
                    this using FiktionConfig.Collection.size(4)
                    element using FiktionConfig.Int.range(100..200)
                }
            }

        assertTrue(value.keys.all { key -> key.size == 2 })
        assertTrue(value.keys.flatten().all { element -> element in 10..20 })
        assertTrue(value.values.all { value -> value.size == 4 })
        assertTrue(value.values.flatten().all { element -> element in 100..200 })
    }

    @Test
    fun `per-call property target can configure generated container parts`() {
        val fiktion =
            Fiktion {
                register(configCatalogMetadata())
            }

        val catalog =
            fiktion.fake<ConfigCatalog>(seed = 1) {
                property(ConfigCatalog::counts) using FiktionConfig.Collection.size(3)
                property(ConfigCatalog::counts).element using FiktionConfig.Int.range(10..20)
            }

        assertEquals(3, catalog.counts.size)
        assertTrue(catalog.counts.all { element -> element in 10..20 })
    }

    @Test
    fun `per-call map property target can generate keys and values with existing indexes`() {
        val fiktion =
            Fiktion {
                register(configIndexMetadata())
            }

        val index =
            fiktion.fake<ConfigIndex>(seed = 1) {
                property(ConfigIndex::entries) {
                    this using FiktionConfig.Map.size(3)
                    key generatesBy { "key-$index" }
                    value generatesBy { index }
                }
            }

        assertEquals(
            mapOf(
                "key-0" to 0,
                "key-1" to 1,
                "key-2" to 2,
            ),
            index.entries,
        )
    }

    @Test
    fun `builder type target can configure generated container parts`() {
        val fiktion =
            Fiktion {
                type<List<Int>> {
                    this using FiktionConfig.Collection.size(3)
                    element using FiktionConfig.Int.range(10..20)
                }
            }

        val value = fiktion.fake<List<Int>>(seed = 1)

        assertEquals(3, value.size)
        assertTrue(value.all { element -> element in 10..20 })
    }

    @Test
    fun `instance config applies to nested collection elements`() {
        val fiktion =
            Fiktion {
                this using FiktionConfig.Collection.size(5)
                this using FiktionConfig.Int.range(10..20)
            }

        val value = fiktion.fake<List<Int>>(seed = 1)

        assertEquals(5, value.size)
        assertTrue(value.all { element -> element in 10..20 })
    }

    @Test
    fun `property config applies to the selected property`() {
        val fiktion =
            Fiktion {
                register(configUserMetadata())
                register(configProfileMetadata())
            }

        val user =
            fiktion.fake<ConfigUser>(seed = 1) {
                ConfigUser::id using FiktionConfig.String.length(4)
            }

        assertEquals(4, user.id.length)
    }

    @Test
    fun `nested property config applies to the selected nested property`() {
        val fiktion =
            Fiktion {
                register(configUserMetadata())
                register(configProfileMetadata())
            }

        val user =
            fiktion.fake<ConfigUser>(seed = 1) {
                ConfigUser::profile {
                    ConfigProfile::nickname using FiktionConfig.String.length(6)
                }
            }

        assertEquals(6, user.profile.nickname.length)
    }

    @Test
    fun `property config overrides broader config`() {
        val fiktion =
            Fiktion {
                register(configUserMetadata())
                register(configProfileMetadata())
                this using FiktionConfig.String.length(8)
            }

        val user =
            fiktion.fake<ConfigUser>(seed = 1) {
                ConfigUser::id using FiktionConfig.String.length(4)
            }

        assertEquals(4, user.id.length)
        assertEquals(8, user.profile.nickname.length)
    }

    @Test
    fun `string charset config changes built-in generator defaults`() {
        val value =
            fake<String>(seed = 1) {
                this using FiktionConfig.String.length(8)
                this using FiktionConfig.String.charset(FiktionCharset.Numeric)
            }

        assertEquals(8, value.length)
        assertTrue(value.all { char -> char.isDigit() })
    }
}

private data class ConfigUser(
    val id: String,
    val profile: ConfigProfile = ConfigProfile(nickname = ""),
)

private data class ConfigProfile(
    val nickname: String,
)

private data class ConfigCatalog(
    val counts: List<Int>,
)

private data class ConfigIndex(
    val entries: Map<String, Int>,
)

private fun configUserMetadata(): FiktionObjectMetadata<ConfigUser> =
    FiktionObjectMetadata(
        type = typeOf<ConfigUser>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "id", type = typeOf<String>()),
                FiktionObjectProperty(name = "profile", type = typeOf<ConfigProfile>()),
            ),
    ) { values ->
        ConfigUser(
            id = values[0].valueOrDefault(defaultValue = null) as String,
            profile = values[1].valueOrDefault(defaultValue = null) as ConfigProfile,
        )
    }

private fun configProfileMetadata(): FiktionObjectMetadata<ConfigProfile> =
    FiktionObjectMetadata(
        type = typeOf<ConfigProfile>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "nickname", type = typeOf<String>()),
            ),
    ) { values ->
        ConfigProfile(nickname = values[0].valueOrDefault(defaultValue = null) as String)
    }

@Suppress("UNCHECKED_CAST")
private fun configCatalogMetadata(): FiktionObjectMetadata<ConfigCatalog> =
    FiktionObjectMetadata(
        type = typeOf<ConfigCatalog>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "counts", type = typeOf<List<Int>>()),
            ),
    ) { values ->
        ConfigCatalog(counts = values[0].valueOrDefault(defaultValue = null) as List<Int>)
    }

@Suppress("UNCHECKED_CAST")
private fun configIndexMetadata(): FiktionObjectMetadata<ConfigIndex> =
    FiktionObjectMetadata(
        type = typeOf<ConfigIndex>(),
        properties =
            listOf(
                FiktionObjectProperty(name = "entries", type = typeOf<Map<String, Int>>()),
            ),
    ) { values ->
        ConfigIndex(entries = values[0].valueOrDefault(defaultValue = null) as Map<String, Int>)
    }
