package dev.s7a.fiktion.addon.java

import dev.s7a.fiktion.CannotGenerateException
import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.addon.java.generators.arrayDeque
import dev.s7a.fiktion.addon.java.generators.arrayList
import dev.s7a.fiktion.addon.java.generators.atomicReference
import dev.s7a.fiktion.addon.java.generators.completableFuture
import dev.s7a.fiktion.addon.java.generators.concurrentHashMap
import dev.s7a.fiktion.addon.java.generators.concurrentLinkedQueue
import dev.s7a.fiktion.addon.java.generators.concurrentSkipListMap
import dev.s7a.fiktion.addon.java.generators.copyOnWriteArrayList
import dev.s7a.fiktion.addon.java.generators.enumMap
import dev.s7a.fiktion.addon.java.generators.enumSet
import dev.s7a.fiktion.addon.java.generators.hashMap
import dev.s7a.fiktion.addon.java.generators.hashSet
import dev.s7a.fiktion.addon.java.generators.identityHashMap
import dev.s7a.fiktion.addon.java.generators.linkedHashMap
import dev.s7a.fiktion.addon.java.generators.linkedHashSet
import dev.s7a.fiktion.addon.java.generators.linkedList
import dev.s7a.fiktion.addon.java.generators.optional
import dev.s7a.fiktion.addon.java.generators.priorityQueue
import dev.s7a.fiktion.addon.java.generators.queue
import dev.s7a.fiktion.addon.java.generators.treeMap
import dev.s7a.fiktion.addon.java.generators.treeSet
import dev.s7a.fiktion.addon.java.generators.weakHashMap
import dev.s7a.fiktion.andValues
import dev.s7a.fiktion.auto
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generatesEach
import dev.s7a.fiktion.generatesKeys
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import dev.s7a.fiktion.invoke
import java.io.EOFException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.UncheckedIOException
import java.io.Writer
import java.math.BigDecimal
import java.math.BigInteger
import java.net.HttpCookie
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MalformedURLException
import java.net.Proxy
import java.net.SocketAddress
import java.net.SocketException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.LongBuffer
import java.nio.ShortBuffer
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.nio.file.attribute.PosixFilePermission
import java.security.MessageDigest
import java.security.Principal
import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import java.sql.SQLTimeoutException
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.BitSet
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Deque
import java.util.EnumMap
import java.util.EnumSet
import java.util.HashMap
import java.util.HashSet
import java.util.IdentityHashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.Locale
import java.util.NavigableMap
import java.util.NavigableSet
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt
import java.util.OptionalLong
import java.util.PriorityQueue
import java.util.Properties
import java.util.Queue
import java.util.Random
import java.util.Scanner
import java.util.SortedMap
import java.util.SortedSet
import java.util.SplittableRandom
import java.util.StringJoiner
import java.util.TimeZone
import java.util.TreeMap
import java.util.TreeSet
import java.util.UUID
import java.util.WeakHashMap
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.DoubleAccumulator
import java.util.concurrent.atomic.DoubleAdder
import java.util.concurrent.atomic.LongAccumulator
import java.util.concurrent.atomic.LongAdder
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.BiPredicate
import java.util.function.BinaryOperator
import java.util.function.BooleanSupplier
import java.util.function.Consumer
import java.util.function.DoubleSupplier
import java.util.function.IntSupplier
import java.util.function.LongSupplier
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.UnaryOperator
import java.util.logging.Level
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import java.sql.Date as SqlDate
import java.sql.Time as SqlTime
import java.sql.Timestamp as SqlTimestamp
import java.util.function.Function as JavaFunction

class JavaFiktionAddonTest {
    @Test
    fun `java add-on rules are inactive until the add-on is installed`() {
        assertFailsWith<CannotGenerateException> {
            fake<Instant>(seed = 123)
        }
    }

    @Test
    fun `installed java add-on uses configured ranges`() {
        val instant = Instant.parse("2026-05-31T00:00:00Z")
        val date = LocalDate.of(2026, 5, 31)
        val time = LocalTime.of(9, 30)
        val fiktion =
            Fiktion {
                install(JavaFiktionAddon)
                this using JavaFiktionConfig.Instant.epochSeconds(instant.epochSecond..instant.epochSecond)
                this using JavaFiktionConfig.Instant.nanosecond(123..123)
                this using JavaFiktionConfig.LocalDate.epochDays(date.toEpochDay()..date.toEpochDay())
                this using JavaFiktionConfig.LocalTime.nanosecondsOfDay(time.toNanoOfDay()..time.toNanoOfDay())
                this using JavaFiktionConfig.ZoneOffset.hours(9..9)
                this using JavaFiktionConfig.Duration.millis(42L..42L)
            }

        assertEquals(instant.plusNanos(123), fiktion.fake<Instant>(seed = 123))
        assertEquals(date, fiktion.fake<LocalDate>(seed = 123))
        assertEquals(time, fiktion.fake<LocalTime>(seed = 123))
        assertEquals(ZoneOffset.ofHours(9), fiktion.fake<ZoneOffset>(seed = 123))
        assertEquals(Duration.ofMillis(42), fiktion.fake<Duration>(seed = 123))
    }

    @Test
    fun `installed java add-on uses core collection and map size configs`() {
        val fiktion =
            Fiktion {
                install(JavaFiktionAddon)
                this using FiktionConfig.Collection.size(4..4)
                this using FiktionConfig.Map.size(5..5)
            }

        assertEquals(4, fiktion.fake<ArrayList<Int>>(seed = 123).size)
        assertEquals(4, fiktion.fake<ArrayDeque<Int>>(seed = 123).size)
        assertEquals(5, fiktion.fake<HashMap<Int, Long>>(seed = 123).size)
        assertEquals(5, fiktion.fake<ConcurrentHashMap<Int, Long>>(seed = 123).size)
    }

    @Test
    fun `installed java add-on generates supported java types`() {
        val fiktion =
            Fiktion {
                install(JavaFiktionAddon)
                type<JavaAddonStatus>() generates JavaAddonStatus.ACTIVE
            }

        assertTrue(fiktion.fake<Instant>(seed = 123) in MIN_INSTANT..MAX_INSTANT)
        assertTrue(fiktion.fake<LocalDate>(seed = 123) in MIN_DATE..MAX_DATE)
        assertTrue(fiktion.fake<LocalTime>(seed = 123) >= LocalTime.MIN)
        assertTrue(fiktion.fake<LocalDateTime>(seed = 123).toLocalDate() in MIN_DATE..MAX_DATE)
        assertTrue(fiktion.fake<Year>(seed = 123).value in 1900..2100)
        assertTrue(fiktion.fake<YearMonth>(seed = 123).year in 1900..2100)
        assertTrue(fiktion.fake<MonthDay>(seed = 123).dayOfMonth in 1..31)
        assertTrue(fiktion.fake<Month>(seed = 123).value in 1..12)
        assertTrue(fiktion.fake<DayOfWeek>(seed = 123).value in 1..7)
        assertTrue(fiktion.fake<OffsetDateTime>(seed = 123).offset.totalSeconds in MIN_ZONE_OFFSET_SECONDS..MAX_ZONE_OFFSET_SECONDS)
        assertTrue(fiktion.fake<OffsetTime>(seed = 123).offset.totalSeconds in MIN_ZONE_OFFSET_SECONDS..MAX_ZONE_OFFSET_SECONDS)
        assertTrue(
            fiktion
                .fake<ZonedDateTime>(seed = 123)
                .zone.id
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<Clock>(seed = 123)
                .zone.id
                .isNotBlank(),
        )
        assertTrue(fiktion.fake<ZoneId>(seed = 123).id.isNotBlank())
        assertTrue(fiktion.fake<Duration>(seed = 123) in MIN_DURATION..MAX_DURATION)
        assertTrue(fiktion.fake<Period>(seed = 123).years in -200..200)
        assertTrue(fiktion.fake<Date>(seed = 123).toInstant() in MIN_INSTANT..MAX_INSTANT)
        assertTrue(fiktion.fake<SqlDate>(seed = 123).toLocalDate() in MIN_DATE..MAX_DATE)
        assertTrue(fiktion.fake<SqlTime>(seed = 123).toLocalTime() >= LocalTime.MIN)
        assertTrue(fiktion.fake<SqlTimestamp>(seed = 123).toInstant() in MIN_INSTANT..MAX_INSTANT)
        assertTrue(fiktion.fake<Calendar>(seed = 123).toInstant() in MIN_INSTANT..MAX_INSTANT)
        assertTrue(fiktion.fake<DateFormat>(seed = 123).format(Date(0)).isNotBlank())
        assertTrue(fiktion.fake<NumberFormat>(seed = 123).format(1234).isNotBlank())
        assertTrue(fiktion.fake<SimpleDateFormat>(seed = 123).toPattern().isNotBlank())
        assertTrue(fiktion.fake<DateTimeFormatter>(seed = 123).toString().isNotBlank())
        assertTrue(fiktion.fake<ChronoUnit>(seed = 123) in ChronoUnit.entries)
        assertEquals(fiktion.fake<UUID>(seed = 123), fiktion.fake<UUID>(seed = 123))
        assertTrue(fiktion.fake<Class<*>>(seed = 123).name.isNotBlank())
        assertTrue(fiktion.fake<URI>(seed = 123).host.isNotBlank())
        assertTrue(fiktion.fake<URL>(seed = 123).host.isNotBlank())
        assertTrue(fiktion.fake<HttpCookie>(seed = 123).name.isNotBlank())
        assertTrue(fiktion.fake<File>(seed = 123).name.contains("."))
        assertTrue(
            fiktion
                .fake<Path>(seed = 123)
                .fileName
                .toString()
                .contains("."),
        )
        assertTrue(fiktion.fake<FileTime>(seed = 123).toInstant() in MIN_INSTANT..MAX_INSTANT)
        assertTrue(fiktion.fake<PosixFilePermission>(seed = 123) in PosixFilePermission.entries)
        assertTrue(fiktion.fake<InputStream>(seed = 123).readBytes().isNotEmpty())
        assertTrue(fiktion.fake<OutputStream>(seed = 123).toString().isNotEmpty())
        assertTrue(fiktion.fake<Reader>(seed = 123).readText().isNotEmpty())
        assertTrue(fiktion.fake<Writer>(seed = 123).toString().isNotEmpty())
        assertTrue(
            fiktion
                .fake<IOException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<EOFException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<FileNotFoundException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<UncheckedIOException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(fiktion.fake<ByteBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<CharBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<ShortBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<IntBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<LongBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<FloatBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<DoubleBuffer>(seed = 123).remaining() > 0)
        assertTrue(fiktion.fake<ZipEntry>(seed = 123).name.contains("."))
        assertTrue(fiktion.fake<Properties>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<Queue<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<Deque<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayDeque<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayList<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedList<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<HashSet<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedHashSet<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<SortedSet<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<NavigableSet<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<TreeSet<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<PriorityQueue<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<CopyOnWriteArrayList<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<HashMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedHashMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<IdentityHashMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<WeakHashMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<SortedMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<NavigableMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<TreeMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentHashMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentLinkedQueue<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentLinkedDeque<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayBlockingQueue<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedBlockingQueue<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentSkipListSet<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<PriorityBlockingQueue<String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentSkipListMap<String, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<EnumSet<JavaAddonStatus>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<EnumMap<JavaAddonStatus, String>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<Queue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<Deque<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayDeque<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayList<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedList<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<HashSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedHashSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<SortedSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<NavigableSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<TreeSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<PriorityQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<CopyOnWriteArrayList<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<HashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<IdentityHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<WeakHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<SortedMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<NavigableMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<TreeMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentLinkedQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentLinkedDeque<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayBlockingQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedBlockingQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentSkipListSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<PriorityBlockingQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentSkipListMap<Int, Long>>(seed = 123).isNotEmpty())
        assertEquals(fiktion.fake<Random>(seed = 123).nextInt(), fiktion.fake<Random>(seed = 123).nextInt())
        assertEquals(fiktion.fake<SplittableRandom>(seed = 123).nextInt(), fiktion.fake<SplittableRandom>(seed = 123).nextInt())
        assertEquals(fiktion.fake<BitSet>(seed = 123), fiktion.fake<BitSet>(seed = 123))
        assertTrue(fiktion.fake<StringJoiner>(seed = 123).toString().isNotBlank())
        assertTrue(fiktion.fake<Scanner>(seed = 123).hasNext())
        assertEquals(fiktion.fake<BigInteger>(seed = 123), fiktion.fake<BigInteger>(seed = 123))
        assertEquals(fiktion.fake<BigDecimal>(seed = 123), fiktion.fake<BigDecimal>(seed = 123))
        assertEquals(fiktion.fake<AtomicBoolean>(seed = 123).get(), fiktion.fake<AtomicBoolean>(seed = 123).get())
        assertEquals(fiktion.fake<AtomicInteger>(seed = 123).get(), fiktion.fake<AtomicInteger>(seed = 123).get())
        assertEquals(fiktion.fake<AtomicLong>(seed = 123).get(), fiktion.fake<AtomicLong>(seed = 123).get())
        assertEquals(fiktion.fake<LongAdder>(seed = 123).sum(), fiktion.fake<LongAdder>(seed = 123).sum())
        assertEquals(fiktion.fake<DoubleAdder>(seed = 123).sum(), fiktion.fake<DoubleAdder>(seed = 123).sum())
        assertEquals(
            fiktion.fake<LongAccumulator>(seed = 123).get(),
            fiktion.fake<LongAccumulator>(seed = 123).get(),
        )
        assertEquals(
            fiktion.fake<DoubleAccumulator>(seed = 123).get(),
            fiktion.fake<DoubleAccumulator>(seed = 123).get(),
        )
        assertEquals(fiktion.fake<AtomicReference<String>>(seed = 123).get(), fiktion.fake<AtomicReference<String>>(seed = 123).get())
        assertEquals(fiktion.fake<AtomicReference<Int>>(seed = 123).get(), fiktion.fake<AtomicReference<Int>>(seed = 123).get())
        assertTrue(fiktion.fake<Locale>(seed = 123).toLanguageTag().isNotBlank())
        assertTrue(fiktion.fake<Currency>(seed = 123).currencyCode.isNotBlank())
        assertTrue(fiktion.fake<TimeZone>(seed = 123).id.isNotBlank())
        assertTrue(fiktion.fake<Charset>(seed = 123).name().isNotBlank())
        assertTrue(fiktion.fake<InetAddress>(seed = 123).isLoopbackAddress)
        assertTrue(fiktion.fake<InetSocketAddress>(seed = 123).address.isLoopbackAddress)
        assertTrue(fiktion.fake<SocketAddress>(seed = 123) is InetSocketAddress)
        assertTrue(fiktion.fake<Proxy>(seed = 123).type() in Proxy.Type.entries)
        assertTrue(fiktion.fake<Proxy.Type>(seed = 123) in Proxy.Type.entries)
        assertTrue(
            fiktion
                .fake<MalformedURLException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(fiktion.fake<URISyntaxException>(seed = 123).reason.isNotBlank())
        assertTrue(
            fiktion
                .fake<SocketException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<UnknownHostException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(fiktion.fake<Pattern>(seed = 123).pattern().isNotBlank())
        assertTrue(fiktion.fake<Level>(seed = 123) in JAVA_LOG_LEVELS)
        assertTrue(fiktion.fake<MessageDigest>(seed = 123).algorithm.isNotBlank())
        assertTrue(fiktion.fake<Principal>(seed = 123).name.isNotBlank())
        assertTrue(fiktion.fake<TimeUnit>(seed = 123) in TimeUnit.entries)
        assertEquals(fiktion.fake<OptionalInt>(seed = 123), fiktion.fake<OptionalInt>(seed = 123))
        assertEquals(fiktion.fake<OptionalLong>(seed = 123), fiktion.fake<OptionalLong>(seed = 123))
        assertEquals(fiktion.fake<OptionalDouble>(seed = 123), fiktion.fake<OptionalDouble>(seed = 123))
        assertEquals(fiktion.fake<Optional<String>>(seed = 123), fiktion.fake<Optional<String>>(seed = 123))
        assertEquals(fiktion.fake<Optional<Int>>(seed = 123), fiktion.fake<Optional<Int>>(seed = 123))
        fiktion.fake<Runnable>(seed = 123).run()
        assertEquals(fiktion.fake<Supplier<String>>(seed = 123).get(), fiktion.fake<Supplier<String>>(seed = 123).get())
        fiktion.fake<Consumer<String>>(seed = 123).accept("ignored")
        fiktion.fake<BiConsumer<String, Int>>(seed = 123).accept("ignored", 1)
        assertEquals(
            fiktion.fake<JavaFunction<String, Int>>(seed = 123).apply("ignored"),
            fiktion.fake<JavaFunction<String, Int>>(seed = 123).apply("ignored"),
        )
        assertEquals(
            fiktion.fake<BiFunction<String, Int, Long>>(seed = 123).apply("ignored", 1),
            fiktion.fake<BiFunction<String, Int, Long>>(seed = 123).apply("ignored", 1),
        )
        assertEquals(
            fiktion.fake<Predicate<String>>(seed = 123).test("ignored"),
            fiktion.fake<Predicate<String>>(seed = 123).test("ignored"),
        )
        assertEquals(
            fiktion.fake<BiPredicate<String, Int>>(seed = 123).test("ignored", 1),
            fiktion.fake<BiPredicate<String, Int>>(seed = 123).test("ignored", 1),
        )
        assertEquals(
            fiktion.fake<UnaryOperator<String>>(seed = 123).apply("ignored"),
            fiktion.fake<UnaryOperator<String>>(seed = 123).apply("ignored"),
        )
        assertEquals(
            fiktion.fake<BinaryOperator<String>>(seed = 123).apply("ignored", "ignored"),
            fiktion.fake<BinaryOperator<String>>(seed = 123).apply("ignored", "ignored"),
        )
        assertEquals(fiktion.fake<BooleanSupplier>(seed = 123).asBoolean, fiktion.fake<BooleanSupplier>(seed = 123).asBoolean)
        assertEquals(fiktion.fake<IntSupplier>(seed = 123).asInt, fiktion.fake<IntSupplier>(seed = 123).asInt)
        assertEquals(fiktion.fake<LongSupplier>(seed = 123).asLong, fiktion.fake<LongSupplier>(seed = 123).asLong)
        assertEquals(fiktion.fake<DoubleSupplier>(seed = 123).asDouble, fiktion.fake<DoubleSupplier>(seed = 123).asDouble)
        assertTrue(
            fiktion
                .fake<SQLException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<SQLTimeoutException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertTrue(
            fiktion
                .fake<SQLIntegrityConstraintViolationException>(seed = 123)
                .message
                .orEmpty()
                .isNotBlank(),
        )
        assertEquals(
            fiktion.fake<CompletableFuture<String>>(seed = 123).getNow(null),
            fiktion.fake<CompletableFuture<String>>(seed = 123).getNow(null),
        )
        assertEquals(
            fiktion.fake<CompletableFuture<Int>>(seed = 123).getNow(null),
            fiktion.fake<CompletableFuture<Int>>(seed = 123).getNow(null),
        )
    }

    @Test
    fun `local rules override java add-on rules`() {
        val fixed = Instant.parse("2026-05-28T00:00:00Z")
        val fiktion =
            Fiktion {
                install(JavaFiktionAddon)
                type<Instant>() generates fixed
            }

        assertEquals(fixed, fiktion.fake<Instant>(seed = 123))
    }

    @Test
    fun `exact java functional interface rules replace add-on type family rules`() {
        val fiktion =
            Fiktion {
                install(JavaFiktionAddon)
                type<Supplier<String>>() generates Supplier { "configured" }
                type<JavaFunction<String, Int>>() generates JavaFunction { 42 }
            }

        assertEquals("configured", fiktion.fake<Supplier<String>>(seed = 123).get())
        assertEquals(42, fiktion.fake<JavaFunction<String, Int>>(seed = 123).apply("ignored"))
    }

    @Test
    fun `java collection converters support collection and map rules`() {
        val fiktion =
            Fiktion {
                install(JavaFiktionAddon)
                type<ArrayDeque<Int>>() generatesEach {
                    int()
                } withSize 2
                type<HashSet<Int>>() generatesEach {
                    int()
                } withSize 2
                type<HashMap<Int, Long>>() generatesKeys {
                    int()
                } andValues {
                    long()
                } withSize 2
                type<TreeSet<Int>>() generatesEach {
                    int()
                } withSize 2
                type<TreeMap<Int, Long>>() generatesKeys {
                    int()
                } andValues {
                    long()
                } withSize 2
                type<ConcurrentLinkedQueue<Int>>() generatesEach {
                    int()
                } withSize 2
                type<ConcurrentSkipListMap<Int, Long>>() generatesKeys {
                    int()
                } andValues {
                    long()
                } withSize 2
                type<ArrayDeque<String>>() generates auto withSize 2
                type<ConcurrentHashMap<Int, Long>>() generates auto withSize 2
            }

        assertEquals(2, fiktion.fake<ArrayDeque<Int>>(seed = 123).size)
        assertEquals(2, fiktion.fake<HashSet<Int>>(seed = 123).size)
        assertEquals(2, fiktion.fake<HashMap<Int, Long>>(seed = 123).size)
        assertEquals(2, fiktion.fake<TreeSet<Int>>(seed = 123).size)
        assertEquals(2, fiktion.fake<TreeMap<Int, Long>>(seed = 123).size)
        assertEquals(2, fiktion.fake<ConcurrentLinkedQueue<Int>>(seed = 123).size)
        assertEquals(2, fiktion.fake<ConcurrentSkipListMap<Int, Long>>(seed = 123).size)
        assertEquals(2, fiktion.fake<ArrayDeque<String>>(seed = 123).size)
        assertTrue(fiktion.fake<ConcurrentHashMap<Int, Long>>(seed = 123).isNotEmpty())
    }

    @Test
    fun `generic java generators accept caller-provided element generators`() {
        val fiktion =
            Fiktion {
                type<ArrayDeque<Int>>() generatesBy {
                    arrayDeque { int() }
                }
                type<Queue<Int>>() generatesBy {
                    queue { int() }
                }
                type<ArrayList<Int>>() generatesBy {
                    arrayList { int() }
                }
                type<LinkedList<Int>>() generatesBy {
                    linkedList { int() }
                }
                type<HashSet<Int>>() generatesBy {
                    hashSet { int() }
                }
                type<LinkedHashSet<Int>>() generatesBy {
                    linkedHashSet { int() }
                }
                type<TreeSet<Int>>() generatesBy {
                    treeSet { int() }
                }
                type<PriorityQueue<Int>>() generatesBy {
                    priorityQueue { int() }
                }
                type<CopyOnWriteArrayList<Int>>() generatesBy {
                    copyOnWriteArrayList { int() }
                }
                type<HashMap<Int, Long>>() generatesBy {
                    hashMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<LinkedHashMap<Int, Long>>() generatesBy {
                    linkedHashMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<IdentityHashMap<Int, Long>>() generatesBy {
                    identityHashMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<WeakHashMap<Int, Long>>() generatesBy {
                    weakHashMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<TreeMap<Int, Long>>() generatesBy {
                    treeMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<EnumSet<JavaAddonStatus>>() generatesBy {
                    enumSet { JavaAddonStatus.ACTIVE }
                }
                type<EnumMap<JavaAddonStatus, Long>>() generatesBy {
                    enumMap(
                        key = { JavaAddonStatus.ACTIVE },
                        value = { long() },
                    )
                }
                type<ConcurrentHashMap<Int, Long>>() generatesBy {
                    concurrentHashMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<ConcurrentLinkedQueue<Int>>() generatesBy {
                    concurrentLinkedQueue { int() }
                }
                type<ConcurrentSkipListMap<Int, Long>>() generatesBy {
                    concurrentSkipListMap(
                        key = { int() },
                        value = { long() },
                    )
                }
                type<AtomicReference<Int>>() generatesBy {
                    atomicReference { int() }
                }
                type<Optional<Int>>() generatesBy {
                    optional { int() }
                }
                type<CompletableFuture<Int>>() generatesBy {
                    completableFuture { int() }
                }
            }

        assertTrue(fiktion.fake<ArrayDeque<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<Queue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ArrayList<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedList<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<HashSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedHashSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<TreeSet<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<PriorityQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<CopyOnWriteArrayList<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<HashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<LinkedHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<IdentityHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<WeakHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<TreeMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<EnumSet<JavaAddonStatus>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<EnumMap<JavaAddonStatus, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentHashMap<Int, Long>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentLinkedQueue<Int>>(seed = 123).isNotEmpty())
        assertTrue(fiktion.fake<ConcurrentSkipListMap<Int, Long>>(seed = 123).isNotEmpty())
        assertEquals(fiktion.fake<AtomicReference<Int>>(seed = 123).get(), fiktion.fake<AtomicReference<Int>>(seed = 123).get())
        assertEquals(fiktion.fake<Optional<Int>>(seed = 123), fiktion.fake<Optional<Int>>(seed = 123))
        assertEquals(
            fiktion.fake<CompletableFuture<Int>>(seed = 123).getNow(null),
            fiktion.fake<CompletableFuture<Int>>(seed = 123).getNow(null),
        )
    }
}

private enum class JavaAddonStatus {
    ACTIVE,
    INACTIVE,
}

private val MIN_INSTANT: Instant = Instant.parse("1900-01-01T00:00:00Z")

private val MAX_INSTANT: Instant = Instant.parse("2100-12-31T23:59:59Z")

private val MIN_DATE: LocalDate = LocalDate.of(1900, 1, 1)

private val MAX_DATE: LocalDate = LocalDate.of(2100, 12, 31)

private val MIN_DURATION: Duration = Duration.ofMillis(-3_153_600_000_000L)

private val MAX_DURATION: Duration = Duration.ofMillis(3_153_600_000_000L)

private const val MIN_ZONE_OFFSET_SECONDS: Int = -43_200

private const val MAX_ZONE_OFFSET_SECONDS: Int = 50_400

private val JAVA_LOG_LEVELS: List<Level> =
    listOf(
        Level.OFF,
        Level.SEVERE,
        Level.WARNING,
        Level.INFO,
        Level.CONFIG,
        Level.FINE,
        Level.FINER,
        Level.FINEST,
        Level.ALL,
    )
