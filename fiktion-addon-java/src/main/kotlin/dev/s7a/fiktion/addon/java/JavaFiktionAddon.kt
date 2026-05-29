package dev.s7a.fiktion.addon.java

import dev.s7a.fiktion.FiktionAddon
import dev.s7a.fiktion.FiktionAddonBuilder
import dev.s7a.fiktion.addon.java.generators.arrayBlockingQueue
import dev.s7a.fiktion.addon.java.generators.arrayDeque
import dev.s7a.fiktion.addon.java.generators.arrayList
import dev.s7a.fiktion.addon.java.generators.atomicBoolean
import dev.s7a.fiktion.addon.java.generators.atomicInteger
import dev.s7a.fiktion.addon.java.generators.atomicLong
import dev.s7a.fiktion.addon.java.generators.atomicReference
import dev.s7a.fiktion.addon.java.generators.bigDecimal
import dev.s7a.fiktion.addon.java.generators.bigInteger
import dev.s7a.fiktion.addon.java.generators.bitSet
import dev.s7a.fiktion.addon.java.generators.byteBuffer
import dev.s7a.fiktion.addon.java.generators.calendar
import dev.s7a.fiktion.addon.java.generators.charBuffer
import dev.s7a.fiktion.addon.java.generators.charset
import dev.s7a.fiktion.addon.java.generators.chronoUnit
import dev.s7a.fiktion.addon.java.generators.clock
import dev.s7a.fiktion.addon.java.generators.completableFuture
import dev.s7a.fiktion.addon.java.generators.concurrentHashMap
import dev.s7a.fiktion.addon.java.generators.concurrentLinkedDeque
import dev.s7a.fiktion.addon.java.generators.concurrentLinkedQueue
import dev.s7a.fiktion.addon.java.generators.concurrentMap
import dev.s7a.fiktion.addon.java.generators.concurrentSkipListMap
import dev.s7a.fiktion.addon.java.generators.concurrentSkipListSet
import dev.s7a.fiktion.addon.java.generators.copyOnWriteArrayList
import dev.s7a.fiktion.addon.java.generators.currency
import dev.s7a.fiktion.addon.java.generators.date
import dev.s7a.fiktion.addon.java.generators.dateFormat
import dev.s7a.fiktion.addon.java.generators.dateTimeFormatter
import dev.s7a.fiktion.addon.java.generators.dayOfWeek
import dev.s7a.fiktion.addon.java.generators.deque
import dev.s7a.fiktion.addon.java.generators.doubleAccumulator
import dev.s7a.fiktion.addon.java.generators.doubleAdder
import dev.s7a.fiktion.addon.java.generators.doubleBuffer
import dev.s7a.fiktion.addon.java.generators.duration
import dev.s7a.fiktion.addon.java.generators.enumMap
import dev.s7a.fiktion.addon.java.generators.enumSet
import dev.s7a.fiktion.addon.java.generators.eofException
import dev.s7a.fiktion.addon.java.generators.file
import dev.s7a.fiktion.addon.java.generators.fileNotFoundException
import dev.s7a.fiktion.addon.java.generators.fileTime
import dev.s7a.fiktion.addon.java.generators.floatBuffer
import dev.s7a.fiktion.addon.java.generators.hashMap
import dev.s7a.fiktion.addon.java.generators.hashSet
import dev.s7a.fiktion.addon.java.generators.httpCookie
import dev.s7a.fiktion.addon.java.generators.identityHashMap
import dev.s7a.fiktion.addon.java.generators.inetAddress
import dev.s7a.fiktion.addon.java.generators.inetSocketAddress
import dev.s7a.fiktion.addon.java.generators.inputStream
import dev.s7a.fiktion.addon.java.generators.instant
import dev.s7a.fiktion.addon.java.generators.intBuffer
import dev.s7a.fiktion.addon.java.generators.ioException
import dev.s7a.fiktion.addon.java.generators.javaClass
import dev.s7a.fiktion.addon.java.generators.javaRandom
import dev.s7a.fiktion.addon.java.generators.level
import dev.s7a.fiktion.addon.java.generators.linkedBlockingQueue
import dev.s7a.fiktion.addon.java.generators.linkedHashMap
import dev.s7a.fiktion.addon.java.generators.linkedHashSet
import dev.s7a.fiktion.addon.java.generators.linkedList
import dev.s7a.fiktion.addon.java.generators.localDate
import dev.s7a.fiktion.addon.java.generators.localDateTime
import dev.s7a.fiktion.addon.java.generators.localTime
import dev.s7a.fiktion.addon.java.generators.locale
import dev.s7a.fiktion.addon.java.generators.longAccumulator
import dev.s7a.fiktion.addon.java.generators.longAdder
import dev.s7a.fiktion.addon.java.generators.longBuffer
import dev.s7a.fiktion.addon.java.generators.malformedUrlException
import dev.s7a.fiktion.addon.java.generators.messageDigest
import dev.s7a.fiktion.addon.java.generators.month
import dev.s7a.fiktion.addon.java.generators.monthDay
import dev.s7a.fiktion.addon.java.generators.navigableMap
import dev.s7a.fiktion.addon.java.generators.navigableSet
import dev.s7a.fiktion.addon.java.generators.numberFormat
import dev.s7a.fiktion.addon.java.generators.offsetDateTime
import dev.s7a.fiktion.addon.java.generators.offsetTime
import dev.s7a.fiktion.addon.java.generators.optional
import dev.s7a.fiktion.addon.java.generators.optionalDouble
import dev.s7a.fiktion.addon.java.generators.optionalInt
import dev.s7a.fiktion.addon.java.generators.optionalLong
import dev.s7a.fiktion.addon.java.generators.outputStream
import dev.s7a.fiktion.addon.java.generators.path
import dev.s7a.fiktion.addon.java.generators.pattern
import dev.s7a.fiktion.addon.java.generators.period
import dev.s7a.fiktion.addon.java.generators.posixFilePermission
import dev.s7a.fiktion.addon.java.generators.principal
import dev.s7a.fiktion.addon.java.generators.priorityBlockingQueue
import dev.s7a.fiktion.addon.java.generators.priorityQueue
import dev.s7a.fiktion.addon.java.generators.properties
import dev.s7a.fiktion.addon.java.generators.proxy
import dev.s7a.fiktion.addon.java.generators.proxyType
import dev.s7a.fiktion.addon.java.generators.queue
import dev.s7a.fiktion.addon.java.generators.reader
import dev.s7a.fiktion.addon.java.generators.scanner
import dev.s7a.fiktion.addon.java.generators.shortBuffer
import dev.s7a.fiktion.addon.java.generators.simpleDateFormat
import dev.s7a.fiktion.addon.java.generators.socketAddress
import dev.s7a.fiktion.addon.java.generators.socketException
import dev.s7a.fiktion.addon.java.generators.sortedMap
import dev.s7a.fiktion.addon.java.generators.sortedSet
import dev.s7a.fiktion.addon.java.generators.splittableRandom
import dev.s7a.fiktion.addon.java.generators.sqlDate
import dev.s7a.fiktion.addon.java.generators.sqlException
import dev.s7a.fiktion.addon.java.generators.sqlIntegrityConstraintViolationException
import dev.s7a.fiktion.addon.java.generators.sqlTime
import dev.s7a.fiktion.addon.java.generators.sqlTimeoutException
import dev.s7a.fiktion.addon.java.generators.sqlTimestamp
import dev.s7a.fiktion.addon.java.generators.stringJoiner
import dev.s7a.fiktion.addon.java.generators.timeUnit
import dev.s7a.fiktion.addon.java.generators.timeZone
import dev.s7a.fiktion.addon.java.generators.treeMap
import dev.s7a.fiktion.addon.java.generators.treeSet
import dev.s7a.fiktion.addon.java.generators.uncheckedIoException
import dev.s7a.fiktion.addon.java.generators.unknownHostException
import dev.s7a.fiktion.addon.java.generators.uri
import dev.s7a.fiktion.addon.java.generators.uriSyntaxException
import dev.s7a.fiktion.addon.java.generators.url
import dev.s7a.fiktion.addon.java.generators.uuid
import dev.s7a.fiktion.addon.java.generators.weakHashMap
import dev.s7a.fiktion.addon.java.generators.writer
import dev.s7a.fiktion.addon.java.generators.year
import dev.s7a.fiktion.addon.java.generators.yearMonth
import dev.s7a.fiktion.addon.java.generators.zipEntry
import dev.s7a.fiktion.addon.java.generators.zoneId
import dev.s7a.fiktion.addon.java.generators.zoneOffset
import dev.s7a.fiktion.addon.java.generators.zonedDateTime
import dev.s7a.fiktion.generatesBy
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.double
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
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
import java.util.function.DoubleBinaryOperator
import java.util.function.DoubleConsumer
import java.util.function.DoubleFunction
import java.util.function.DoublePredicate
import java.util.function.DoubleSupplier
import java.util.function.DoubleToIntFunction
import java.util.function.DoubleToLongFunction
import java.util.function.DoubleUnaryOperator
import java.util.function.Function
import java.util.function.IntBinaryOperator
import java.util.function.IntConsumer
import java.util.function.IntFunction
import java.util.function.IntPredicate
import java.util.function.IntSupplier
import java.util.function.IntToDoubleFunction
import java.util.function.IntToLongFunction
import java.util.function.IntUnaryOperator
import java.util.function.LongBinaryOperator
import java.util.function.LongConsumer
import java.util.function.LongFunction
import java.util.function.LongPredicate
import java.util.function.LongSupplier
import java.util.function.LongToDoubleFunction
import java.util.function.LongToIntFunction
import java.util.function.LongUnaryOperator
import java.util.function.ObjDoubleConsumer
import java.util.function.ObjIntConsumer
import java.util.function.ObjLongConsumer
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.ToDoubleBiFunction
import java.util.function.ToDoubleFunction
import java.util.function.ToIntBiFunction
import java.util.function.ToIntFunction
import java.util.function.ToLongBiFunction
import java.util.function.ToLongFunction
import java.util.function.UnaryOperator
import java.util.logging.Level
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.sql.Date as SqlDate
import java.sql.Time as SqlTime
import java.sql.Timestamp as SqlTimestamp

/**
 * Fiktion add-on that contributes generation rules for Java standard library types.
 */
public object JavaFiktionAddon : FiktionAddon {
    override val id: String = "java"

    override fun install(builder: FiktionAddonBuilder) {
        with(builder) {
            // java.util collections
            configureCollection<ArrayDeque<*>> { elements ->
                ArrayDeque(elements.filterNotNull())
            }
            configureCollection<ArrayList<*>> { elements ->
                ArrayList(elements)
            }
            configureCollection<Queue<*>> { elements ->
                ArrayDeque(elements.filterNotNull())
            }
            configureCollection<Deque<*>> { elements ->
                ArrayDeque(elements.filterNotNull())
            }
            configureCollection<LinkedList<*>> { elements ->
                LinkedList(elements)
            }
            configureCollection<HashSet<*>> { elements ->
                HashSet(elements)
            }
            configureCollection<LinkedHashSet<*>> { elements ->
                LinkedHashSet(elements)
            }
            configureCollection<SortedSet<*>> { elements ->
                TreeSet(compareBy(Any::toString)).apply {
                    addAll(elements.filterNotNull())
                }
            }
            configureCollection<NavigableSet<*>> { elements ->
                TreeSet(compareBy(Any::toString)).apply {
                    addAll(elements.filterNotNull())
                }
            }
            configureCollection<TreeSet<*>> { elements ->
                TreeSet(compareBy(Any::toString)).apply {
                    addAll(elements.filterNotNull())
                }
            }
            configureCollection<PriorityQueue<*>> { elements ->
                PriorityQueue(compareBy(Any::toString)).apply {
                    addAll(elements.filterNotNull())
                }
            }
            configureCollection<CopyOnWriteArrayList<*>> { elements ->
                CopyOnWriteArrayList(elements)
            }
            configureMap<HashMap<*, *>> { entries ->
                HashMap(entries.toMap())
            }
            configureMap<LinkedHashMap<*, *>> { entries ->
                LinkedHashMap(entries.toMap())
            }
            configureMap<IdentityHashMap<*, *>> { entries ->
                IdentityHashMap(entries.toMap())
            }
            configureMap<WeakHashMap<*, *>> { entries ->
                WeakHashMap(entries.toMap())
            }
            configureMap<SortedMap<*, *>> { entries ->
                TreeMap<Any, Any?>(compareBy(Any::toString)).apply {
                    entries.forEach { (key, value) ->
                        if (key != null) {
                            put(key, value)
                        }
                    }
                }
            }
            configureMap<NavigableMap<*, *>> { entries ->
                TreeMap<Any, Any?>(compareBy(Any::toString)).apply {
                    entries.forEach { (key, value) ->
                        if (key != null) {
                            put(key, value)
                        }
                    }
                }
            }
            configureMap<TreeMap<*, *>> { entries ->
                TreeMap<Any, Any?>(compareBy(Any::toString)).apply {
                    entries.forEach { (key, value) ->
                        if (key != null) {
                            put(key, value)
                        }
                    }
                }
            }

            // java.util.concurrent collections
            configureMap<ConcurrentMap<*, *>> { entries ->
                ConcurrentHashMap<Any, Any>().apply {
                    entries.forEach { (key, value) ->
                        if (key != null && value != null) {
                            put(key, value)
                        }
                    }
                }
            }
            configureMap<ConcurrentHashMap<*, *>> { entries ->
                ConcurrentHashMap<Any, Any>().apply {
                    entries.forEach { (key, value) ->
                        if (key != null && value != null) {
                            put(key, value)
                        }
                    }
                }
            }
            configureCollection<ConcurrentLinkedQueue<*>> { elements ->
                ConcurrentLinkedQueue(elements.filterNotNull())
            }
            configureCollection<ConcurrentLinkedDeque<*>> { elements ->
                ConcurrentLinkedDeque(elements.filterNotNull())
            }
            configureCollection<ArrayBlockingQueue<*>> { elements ->
                val values = elements.filterNotNull()
                ArrayBlockingQueue<Any>(values.size.coerceAtLeast(1)).apply {
                    addAll(values)
                }
            }
            configureCollection<LinkedBlockingQueue<*>> { elements ->
                LinkedBlockingQueue(elements.filterNotNull())
            }
            configureCollection<ConcurrentSkipListSet<*>> { elements ->
                ConcurrentSkipListSet(compareBy(Any::toString)).apply {
                    addAll(elements.filterNotNull())
                }
            }
            configureCollection<PriorityBlockingQueue<*>> { elements ->
                PriorityBlockingQueue<Any>(1, compareBy(Any::toString)).apply {
                    addAll(elements.filterNotNull())
                }
            }
            configureMap<ConcurrentSkipListMap<*, *>> { entries ->
                ConcurrentSkipListMap<Any, Any>(compareBy(Any::toString)).apply {
                    entries.forEach { (key, value) ->
                        if (key != null && value != null) {
                            put(key, value)
                        }
                    }
                }
            }

            // java.time
            type<Instant>() generatesBy {
                instant()
            }
            type<LocalDate>() generatesBy {
                localDate()
            }
            type<LocalTime>() generatesBy {
                localTime()
            }
            type<LocalDateTime>() generatesBy {
                localDateTime()
            }
            type<Year>() generatesBy {
                year()
            }
            type<YearMonth>() generatesBy {
                yearMonth()
            }
            type<MonthDay>() generatesBy {
                monthDay()
            }
            type<Month>() generatesBy {
                month()
            }
            type<DayOfWeek>() generatesBy {
                dayOfWeek()
            }
            type<OffsetDateTime>() generatesBy {
                offsetDateTime()
            }
            type<OffsetTime>() generatesBy {
                offsetTime()
            }
            type<ZonedDateTime>() generatesBy {
                zonedDateTime()
            }
            type<Clock>() generatesBy {
                clock()
            }
            type<ZoneId>() generatesBy {
                zoneId()
            }
            type<ZoneOffset>() generatesBy {
                zoneOffset()
            }
            type<Duration>() generatesBy {
                duration()
            }
            type<Period>() generatesBy {
                period()
            }

            // java.time.format
            type<DateTimeFormatter>() generatesBy {
                dateTimeFormatter()
            }

            // java.time.temporal
            type<ChronoUnit>() generatesBy {
                chronoUnit()
            }

            // java.util
            type<UUID>() generatesBy {
                uuid()
            }
            type<Class<*>>() generatesBy {
                javaClass()
            }
            type<Properties>() generatesBy {
                properties()
            }
            type<Date>() generatesBy {
                date()
            }
            type<Calendar>() generatesBy {
                calendar()
            }
            type<Random>() generatesBy {
                javaRandom()
            }
            type<SplittableRandom>() generatesBy {
                splittableRandom()
            }
            type<BitSet>() generatesBy {
                bitSet()
            }
            type<StringJoiner>() generatesBy {
                stringJoiner()
            }
            type<Scanner>() generatesBy {
                scanner()
            }
            type<Locale>() generatesBy {
                locale()
            }
            type<Currency>() generatesBy {
                currency()
            }
            type<TimeZone>() generatesBy {
                timeZone()
            }
            typeFamily<ArrayDeque<*>>() generatesBy {
                arrayDeque()
            }
            typeFamily<ArrayList<*>>() generatesBy {
                arrayList()
            }
            typeFamily<Queue<*>>() generatesBy {
                queue()
            }
            typeFamily<Deque<*>>() generatesBy {
                deque()
            }
            typeFamily<LinkedList<*>>() generatesBy {
                linkedList()
            }
            typeFamily<HashSet<*>>() generatesBy {
                hashSet()
            }
            typeFamily<LinkedHashSet<*>>() generatesBy {
                linkedHashSet()
            }
            typeFamily<SortedSet<*>>() generatesBy {
                sortedSet()
            }
            typeFamily<NavigableSet<*>>() generatesBy {
                navigableSet()
            }
            typeFamily<TreeSet<*>>() generatesBy {
                treeSet()
            }
            typeFamily<PriorityQueue<*>>() generatesBy {
                priorityQueue()
            }
            typeFamily<CopyOnWriteArrayList<*>>() generatesBy {
                copyOnWriteArrayList()
            }
            typeFamily<HashMap<*, *>>() generatesBy {
                hashMap()
            }
            typeFamily<LinkedHashMap<*, *>>() generatesBy {
                linkedHashMap()
            }
            typeFamily<IdentityHashMap<*, *>>() generatesBy {
                identityHashMap()
            }
            typeFamily<WeakHashMap<*, *>>() generatesBy {
                weakHashMap()
            }
            typeFamily<SortedMap<*, *>>() generatesBy {
                sortedMap()
            }
            typeFamily<NavigableMap<*, *>>() generatesBy {
                navigableMap()
            }
            typeFamily<TreeMap<*, *>>() generatesBy {
                treeMap()
            }
            type<OptionalInt>() generatesBy {
                optionalInt()
            }
            type<OptionalLong>() generatesBy {
                optionalLong()
            }
            type<OptionalDouble>() generatesBy {
                optionalDouble()
            }
            typeFamily<Optional<*>>() generatesBy {
                optional()
            }
            typeFamily<EnumSet<*>>() generatesBy {
                enumSet()
            }
            typeFamily<EnumMap<*, *>>() generatesBy {
                enumMap()
            }

            // java.lang
            type<Runnable>() generatesBy {
                Runnable {}
            }

            // java.util.function
            typeFamily<Supplier<*>>() generatesBy {
                Supplier { fake(0) }
            }
            typeFamily<Consumer<*>>() generatesBy {
                Consumer<Any?> {}
            }
            typeFamily<BiConsumer<*, *>>() generatesBy {
                BiConsumer<Any?, Any?> { _, _ -> }
            }
            typeFamily<Function<*, *>>() generatesBy {
                Function<Any?, Any?> { _ ->
                    fake(1)
                }
            }
            typeFamily<BiFunction<*, *, *>>() generatesBy {
                BiFunction<Any?, Any?, Any?> { _, _ ->
                    fake(2)
                }
            }
            typeFamily<Predicate<*>>() generatesBy {
                Predicate<Any?> { boolean() }
            }
            typeFamily<BiPredicate<*, *>>() generatesBy {
                BiPredicate<Any?, Any?> { _, _ -> boolean() }
            }
            typeFamily<UnaryOperator<*>>() generatesBy {
                UnaryOperator<Any?> { _ ->
                    fake(0)
                }
            }
            typeFamily<BinaryOperator<*>>() generatesBy {
                BinaryOperator<Any?> { _, _ ->
                    fake(0)
                }
            }
            type<BooleanSupplier>() generatesBy {
                BooleanSupplier { boolean() }
            }
            type<IntSupplier>() generatesBy {
                IntSupplier { int() }
            }
            type<LongSupplier>() generatesBy {
                LongSupplier { long() }
            }
            type<DoubleSupplier>() generatesBy {
                DoubleSupplier { double() }
            }
            type<IntConsumer>() generatesBy {
                IntConsumer {}
            }
            type<LongConsumer>() generatesBy {
                LongConsumer {}
            }
            type<DoubleConsumer>() generatesBy {
                DoubleConsumer {}
            }
            typeFamily<ObjIntConsumer<*>>() generatesBy {
                ObjIntConsumer<Any?> { _, _ -> }
            }
            typeFamily<ObjLongConsumer<*>>() generatesBy {
                ObjLongConsumer<Any?> { _, _ -> }
            }
            typeFamily<ObjDoubleConsumer<*>>() generatesBy {
                ObjDoubleConsumer<Any?> { _, _ -> }
            }
            typeFamily<IntFunction<*>>() generatesBy {
                IntFunction<Any?> { _ ->
                    fake(0)
                }
            }
            typeFamily<LongFunction<*>>() generatesBy {
                LongFunction<Any?> { _ ->
                    fake(0)
                }
            }
            typeFamily<DoubleFunction<*>>() generatesBy {
                DoubleFunction<Any?> { _ ->
                    fake(0)
                }
            }
            typeFamily<ToIntFunction<*>>() generatesBy {
                ToIntFunction<Any?> { int() }
            }
            typeFamily<ToLongFunction<*>>() generatesBy {
                ToLongFunction<Any?> { long() }
            }
            typeFamily<ToDoubleFunction<*>>() generatesBy {
                ToDoubleFunction<Any?> { double() }
            }
            typeFamily<ToIntBiFunction<*, *>>() generatesBy {
                ToIntBiFunction<Any?, Any?> { _, _ -> int() }
            }
            typeFamily<ToLongBiFunction<*, *>>() generatesBy {
                ToLongBiFunction<Any?, Any?> { _, _ -> long() }
            }
            typeFamily<ToDoubleBiFunction<*, *>>() generatesBy {
                ToDoubleBiFunction<Any?, Any?> { _, _ -> double() }
            }
            type<IntPredicate>() generatesBy {
                IntPredicate { boolean() }
            }
            type<LongPredicate>() generatesBy {
                LongPredicate { boolean() }
            }
            type<DoublePredicate>() generatesBy {
                DoublePredicate { boolean() }
            }
            type<IntUnaryOperator>() generatesBy {
                IntUnaryOperator { int() }
            }
            type<LongUnaryOperator>() generatesBy {
                LongUnaryOperator { long() }
            }
            type<DoubleUnaryOperator>() generatesBy {
                DoubleUnaryOperator { double() }
            }
            type<IntBinaryOperator>() generatesBy {
                IntBinaryOperator { _, _ -> int() }
            }
            type<LongBinaryOperator>() generatesBy {
                LongBinaryOperator { _, _ -> long() }
            }
            type<DoubleBinaryOperator>() generatesBy {
                DoubleBinaryOperator { _, _ -> double() }
            }
            type<IntToLongFunction>() generatesBy {
                IntToLongFunction { long() }
            }
            type<IntToDoubleFunction>() generatesBy {
                IntToDoubleFunction { double() }
            }
            type<LongToIntFunction>() generatesBy {
                LongToIntFunction { int() }
            }
            type<LongToDoubleFunction>() generatesBy {
                LongToDoubleFunction { double() }
            }
            type<DoubleToIntFunction>() generatesBy {
                DoubleToIntFunction { int() }
            }
            type<DoubleToLongFunction>() generatesBy {
                DoubleToLongFunction { long() }
            }

            // java.text
            type<DateFormat>() generatesBy {
                dateFormat()
            }
            type<NumberFormat>() generatesBy {
                numberFormat()
            }
            type<SimpleDateFormat>() generatesBy {
                simpleDateFormat()
            }

            // java.math
            type<BigInteger>() generatesBy {
                bigInteger()
            }
            type<BigDecimal>() generatesBy {
                bigDecimal()
            }

            // java.util.concurrent
            type<TimeUnit>() generatesBy {
                timeUnit()
            }
            typeFamily<CompletableFuture<*>>() generatesBy {
                completableFuture()
            }
            typeFamily<ConcurrentMap<*, *>>() generatesBy {
                concurrentMap()
            }
            typeFamily<ConcurrentHashMap<*, *>>() generatesBy {
                concurrentHashMap()
            }
            typeFamily<ConcurrentLinkedQueue<*>>() generatesBy {
                concurrentLinkedQueue()
            }
            typeFamily<ConcurrentLinkedDeque<*>>() generatesBy {
                concurrentLinkedDeque()
            }
            typeFamily<ArrayBlockingQueue<*>>() generatesBy {
                arrayBlockingQueue()
            }
            typeFamily<LinkedBlockingQueue<*>>() generatesBy {
                linkedBlockingQueue()
            }
            typeFamily<ConcurrentSkipListSet<*>>() generatesBy {
                concurrentSkipListSet()
            }
            typeFamily<PriorityBlockingQueue<*>>() generatesBy {
                priorityBlockingQueue()
            }
            typeFamily<ConcurrentSkipListMap<*, *>>() generatesBy {
                concurrentSkipListMap()
            }

            // java.util.concurrent.atomic
            type<AtomicBoolean>() generatesBy {
                atomicBoolean()
            }
            type<AtomicInteger>() generatesBy {
                atomicInteger()
            }
            type<AtomicLong>() generatesBy {
                atomicLong()
            }
            type<LongAdder>() generatesBy {
                longAdder()
            }
            type<DoubleAdder>() generatesBy {
                doubleAdder()
            }
            type<LongAccumulator>() generatesBy {
                longAccumulator()
            }
            type<DoubleAccumulator>() generatesBy {
                doubleAccumulator()
            }
            typeFamily<AtomicReference<*>>() generatesBy {
                atomicReference()
            }

            // java.nio
            type<ByteBuffer>() generatesBy {
                byteBuffer()
            }
            type<CharBuffer>() generatesBy {
                charBuffer()
            }
            type<ShortBuffer>() generatesBy {
                shortBuffer()
            }
            type<IntBuffer>() generatesBy {
                intBuffer()
            }
            type<LongBuffer>() generatesBy {
                longBuffer()
            }
            type<FloatBuffer>() generatesBy {
                floatBuffer()
            }
            type<DoubleBuffer>() generatesBy {
                doubleBuffer()
            }

            // java.nio.charset
            type<Charset>() generatesBy {
                charset()
            }

            // java.nio.file
            type<Path>() generatesBy {
                path()
            }
            type<FileTime>() generatesBy {
                fileTime()
            }
            type<PosixFilePermission>() generatesBy {
                posixFilePermission()
            }

            // java.io
            type<File>() generatesBy {
                file()
            }
            type<InputStream>() generatesBy {
                inputStream()
            }
            type<OutputStream>() generatesBy {
                outputStream()
            }
            type<Reader>() generatesBy {
                reader()
            }
            type<Writer>() generatesBy {
                writer()
            }
            type<IOException>() generatesBy {
                ioException()
            }
            type<EOFException>() generatesBy {
                eofException()
            }
            type<FileNotFoundException>() generatesBy {
                fileNotFoundException()
            }
            type<UncheckedIOException>() generatesBy {
                uncheckedIoException()
            }

            // java.net
            type<URI>() generatesBy {
                uri()
            }
            type<URL>() generatesBy {
                url()
            }
            type<HttpCookie>() generatesBy {
                httpCookie()
            }
            type<InetAddress>() generatesBy {
                inetAddress()
            }
            type<InetSocketAddress>() generatesBy {
                inetSocketAddress()
            }
            type<SocketAddress>() generatesBy {
                socketAddress()
            }
            type<Proxy>() generatesBy {
                proxy()
            }
            type<Proxy.Type>() generatesBy {
                proxyType()
            }
            type<MalformedURLException>() generatesBy {
                malformedUrlException()
            }
            type<URISyntaxException>() generatesBy {
                uriSyntaxException()
            }
            type<SocketException>() generatesBy {
                socketException()
            }
            type<UnknownHostException>() generatesBy {
                unknownHostException()
            }

            // java.util.regex
            type<Pattern>() generatesBy {
                pattern()
            }

            // java.util.logging
            type<Level>() generatesBy {
                level()
            }

            // java.security
            type<MessageDigest>() generatesBy {
                messageDigest()
            }
            type<Principal>() generatesBy {
                principal()
            }

            // java.util.zip
            type<ZipEntry>() generatesBy {
                zipEntry()
            }

            // java.sql
            type<SqlDate>() generatesBy {
                sqlDate()
            }
            type<SqlTime>() generatesBy {
                sqlTime()
            }
            type<SqlTimestamp>() generatesBy {
                sqlTimestamp()
            }
            type<SQLException>() generatesBy {
                sqlException()
            }
            type<SQLTimeoutException>() generatesBy {
                sqlTimeoutException()
            }
            type<SQLIntegrityConstraintViolationException>() generatesBy {
                sqlIntegrityConstraintViolationException()
            }
        }
    }
}
