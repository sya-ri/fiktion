package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.CannotGenerateException
import dev.s7a.fiktion.runtime.ExperimentalFiktionApi
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@OptIn(ExperimentalFiktionApi::class)
class GenerateSealedTest {
    @Test
    fun `fake selects a subtype from registered sealed metadata`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                register(textMessageMetadata())
                register(imageMessageMetadata())
                type<String>() generates "generated"
            }

        val message = fiktion.fake<Message>(seed = 123)

        assertTrue(message is TextMessage || message is ImageMessage)
    }

    @Test
    fun `fake changes sealed subtypes with different seeds`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                register(textMessageMetadata())
                register(imageMessageMetadata())
                type<String>() generates "generated"
            }

        val messageTypes = (0L until 20L).map { seed -> fiktion.fake<Message>(seed = seed)::class }.toSet()

        assertTrue(messageTypes.size > 1, "Expected different seeds to select multiple sealed subtypes.")
    }

    @Test
    fun `explicit type rules override registered sealed metadata`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                type<Message>() generates TextMessage(text = "explicit")
            }

        val message = fiktion.fake<Message>(seed = 123)

        assertEquals(TextMessage(text = "explicit"), message)
    }

    @Test
    fun `isolated instance sealed metadata does not leak to other isolated instances`() {
        val registered =
            Fiktion {
                register(messageMetadata(subtypes = listOf(typeOf<TextMessage>())))
                register(textMessageMetadata())
                type<String>() generates "generated"
            }
        val empty = Fiktion()

        assertEquals(TextMessage(text = "generated"), registered.fake<Message>())
        assertFailsWith<CannotGenerateException> {
            empty.fake<Message>(seed = 1)
        }
    }

    @Test
    fun `fake fails when registered sealed metadata has no subtypes`() {
        val fiktion =
            Fiktion {
                register(messageMetadata(subtypes = emptyList()))
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<Message>(seed = 123)
            }

        assertTrue(error.message.orEmpty().contains("registered sealed metadata has no subtypes"))
    }

    @Test
    fun `fake reports selected sealed subtype when subtype metadata is missing`() {
        val fiktion =
            Fiktion {
                register(messageMetadata(subtypes = listOf(typeOf<TextMessage>())))
            }

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<Message>(seed = 123)
            }

        assertTrue(error.message.orEmpty().contains("TextMessage"))
        assertTrue(error.message.orEmpty().contains("no generation rule or metadata is registered"))
    }

    @Test
    fun `fake reports missing metadata for unregistered object types`() {
        val fiktion = Fiktion()

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<TextMessage>(seed = 123)
            }

        assertTrue(error.message.orEmpty().contains("TextMessage"))
        assertTrue(error.message.orEmpty().contains("Register metadata"))
    }

    @Test
    fun `fake keeps built-in generation available without metadata`() {
        val fiktion = Fiktion()

        val value = fiktion.fake<String>(seed = 123)

        assertTrue(value.isNotEmpty())
    }

    /**
     * Returns test metadata for [Message].
     */
    private fun messageMetadata(
        subtypes: List<KType> = listOf(typeOf<TextMessage>(), typeOf<ImageMessage>()),
    ): FiktionSealedMetadata<Message> =
        FiktionSealedMetadata(
            type = typeOf<Message>(),
            subtypes = subtypes,
        )

    /**
     * Returns object metadata for [TextMessage].
     */
    private fun textMessageMetadata(): FiktionObjectMetadata<TextMessage> =
        FiktionObjectMetadata(
            type = typeOf<TextMessage>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "text", type = typeOf<String>()),
                ),
        ) { values ->
            TextMessage(text = values[0].valueOrDefault(defaultValue = null) as String)
        }

    /**
     * Returns object metadata for [ImageMessage].
     */
    private fun imageMessageMetadata(): FiktionObjectMetadata<ImageMessage> =
        FiktionObjectMetadata(
            type = typeOf<ImageMessage>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "url", type = typeOf<String>()),
                ),
        ) { values ->
            ImageMessage(url = values[0].valueOrDefault(defaultValue = null) as String)
        }
}
