package dev.s7a.fiktion

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
    fun `generates auto excludes sealed subtype by class`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                register(textMessageMetadata())
                register(imageMessageMetadata())
                type<String>() generates "generated"
                type<Message>() generates auto excluding ImageMessage::class
            }

        val messages = (0L until 20L).map { seed -> fiktion.fake<Message>(seed = seed) }

        assertTrue(messages.all { message -> message is TextMessage })
    }

    @Test
    fun `generates auto excludes multiple sealed subtypes by class`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                register(textMessageMetadata())
                register(imageMessageMetadata())
                type<Message>() generates auto excluding setOf(TextMessage::class, ImageMessage::class)
            }

        assertFailsWith<FiktionConfigurationException> {
            fiktion.fake<Message>(seed = 123)
        }
    }

    @Test
    fun `generates auto applies cumulative sealed subtype exclusions`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                register(textMessageMetadata())
                register(imageMessageMetadata())
                type<Message>() generates auto excluding TextMessage::class excluding ImageMessage::class
            }

        assertFailsWith<FiktionConfigurationException> {
            fiktion.fake<Message>(seed = 123)
        }
    }

    @Test
    fun `generates auto excludes generic sealed subtype by type`() {
        val fiktion =
            Fiktion {
                register(genericMessageMetadata())
                register(genericStringMessageMetadata())
                register(genericIntMessageMetadata())
                type<String>() generates "generated"
                type<Int>() generates 1
                type<GenericMessageRoot>() generates auto excluding typeOf<GenericMessage<String>>()
            }

        val messages = (0L until 20L).map { seed -> fiktion.fake<GenericMessageRoot>(seed = seed) }

        assertTrue(messages.all { message -> message is GenericMessage<*> && message.value is Int })
    }

    @Test
    fun `generates auto excludes sealed subtypes by predicate`() {
        val fiktion =
            Fiktion {
                register(messageMetadata())
                register(textMessageMetadata())
                register(imageMessageMetadata())
                type<String>() generates "generated"
                type<Message>() generates auto excluding { type: KType -> type.classifier == ImageMessage::class }
            }

        val messages = (0L until 20L).map { seed -> fiktion.fake<Message>(seed = seed) }

        assertTrue(messages.all { message -> message is TextMessage })
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

        assertTrue(error.message.orEmpty().contains("Generated sealed metadata"))
        assertTrue(error.message.orEmpty().contains("Message"))
        assertTrue(error.message.orEmpty().contains("has no concrete subtypes"))
        assertTrue(error.message.orEmpty().contains("type<"))
        assertTrue(error.message.orEmpty().contains("generatesBy { ... }"))
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
        assertTrue(error.message.orEmpty().contains("sealed metadata selected subtype"))
        assertTrue(error.message.orEmpty().contains("No generation rule or generated metadata was found"))
        assertTrue(error.cause is CannotGenerateException)
    }

    @Test
    fun `fake reports missing metadata for unregistered object types`() {
        val fiktion = Fiktion()

        val error =
            assertFailsWith<CannotGenerateException> {
                fiktion.fake<TextMessage>(seed = 123)
            }

        assertTrue(error.message.orEmpty().contains("TextMessage"))
        assertTrue(error.message.orEmpty().contains("No generation rule or generated metadata was found"))
        assertTrue(error.message.orEmpty().contains("Generation request:"))
        assertTrue(error.message.orEmpty().contains("Current Fiktion configuration:"))
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

    private fun genericMessageMetadata(): FiktionSealedMetadata<GenericMessageRoot> =
        FiktionSealedMetadata(
            type = typeOf<GenericMessageRoot>(),
            subtypes = listOf(typeOf<GenericMessage<String>>(), typeOf<GenericMessage<Int>>()),
        )

    private fun genericStringMessageMetadata(): FiktionObjectMetadata<GenericMessage<String>> =
        FiktionObjectMetadata(
            type = typeOf<GenericMessage<String>>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "value", type = typeOf<String>()),
                ),
        ) { values ->
            GenericMessage(values[0].valueOrDefault(defaultValue = null) as String)
        }

    private fun genericIntMessageMetadata(): FiktionObjectMetadata<GenericMessage<Int>> =
        FiktionObjectMetadata(
            type = typeOf<GenericMessage<Int>>(),
            properties =
                listOf(
                    FiktionObjectProperty(name = "value", type = typeOf<Int>()),
                ),
        ) { values ->
            GenericMessage(values[0].valueOrDefault(defaultValue = null) as Int)
        }
}

private sealed interface GenericMessageRoot

private data class GenericMessage<T>(
    val value: T,
) : GenericMessageRoot
