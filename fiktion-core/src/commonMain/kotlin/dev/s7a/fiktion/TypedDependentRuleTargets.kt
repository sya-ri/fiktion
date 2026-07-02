package dev.s7a.fiktion

/**
 * Typed dependent rule targets for property dependency declarations.
 */
public class DependentRuleTarget1<T, D1>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1) -> T): GenerationSpec<T> =
            target.generatesBy { values -> generator(values[0] as D1) }
    }

public class DependentRuleTarget2<T, D1, D2>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2) -> T): GenerationSpec<T> =
            target.generatesBy { values -> generator(values[0] as D1, values[1] as D2) }
    }

public class DependentRuleTarget3<T, D1, D2, D3>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3) -> T): GenerationSpec<T> =
            target.generatesBy { values -> generator(values[0] as D1, values[1] as D2, values[2] as D3) }
    }

public class DependentRuleTarget4<T, D1, D2, D3, D4>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4) -> T): GenerationSpec<T> =
            target.generatesBy { values -> generator(values[0] as D1, values[1] as D2, values[2] as D3, values[3] as D4) }
    }

public class DependentRuleTarget5<T, D1, D2, D3, D4, D5>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5) -> T): GenerationSpec<T> =
            target.generatesBy { values -> generator(values[0] as D1, values[1] as D2, values[2] as D3, values[3] as D4, values[4] as D5) }
    }

public class DependentRuleTarget6<T, D1, D2, D3, D4, D5, D6>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(values[0] as D1, values[1] as D2, values[2] as D3, values[3] as D4, values[4] as D5, values[5] as D6)
            }
    }

public class DependentRuleTarget7<T, D1, D2, D3, D4, D5, D6, D7>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                )
            }
    }

public class DependentRuleTarget8<T, D1, D2, D3, D4, D5, D6, D7, D8>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                )
            }
    }

public class DependentRuleTarget9<T, D1, D2, D3, D4, D5, D6, D7, D8, D9>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                )
            }
    }

public class DependentRuleTarget10<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                )
            }
    }

public class DependentRuleTarget11<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                )
            }
    }

public class DependentRuleTarget12<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12) -> T): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                )
            }
    }

public class DependentRuleTarget13<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                )
            }
    }

public class DependentRuleTarget14<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                )
            }
    }

public class DependentRuleTarget15<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                )
            }
    }

public class DependentRuleTarget16<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                )
            }
    }

public class DependentRuleTarget17<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                    values[16] as D17,
                )
            }
    }

public class DependentRuleTarget18<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                    values[16] as D17,
                    values[17] as D18,
                )
            }
    }

public class DependentRuleTarget19<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                    values[16] as D17,
                    values[17] as D18,
                    values[18] as D19,
                )
            }
    }

public class DependentRuleTarget20<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                    values[16] as D17,
                    values[17] as D18,
                    values[18] as D19,
                    values[19] as D20,
                )
            }
    }

public class DependentRuleTarget21<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                    values[16] as D17,
                    values[17] as D18,
                    values[18] as D19,
                    values[19] as D20,
                    values[20] as D21,
                )
            }
    }

public class DependentRuleTarget22<T, D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22>
    @PublishedApi
    internal constructor(
        private val target: DependentRuleTarget<T>,
    ) {
        /**
         * Generates values for this target by invoking [generator] with dependency values in declaration order.
         */
        @Suppress("UNCHECKED_CAST")
        public infix fun generatesBy(
            generator: FakeContext.(
                D1,
                D2,
                D3,
                D4,
                D5,
                D6,
                D7,
                D8,
                D9,
                D10,
                D11,
                D12,
                D13,
                D14,
                D15,
                D16,
                D17,
                D18,
                D19,
                D20,
                D21,
                D22,
            ) -> T,
        ): GenerationSpec<T> =
            target.generatesBy { values ->
                generator(
                    values[0] as D1,
                    values[1] as D2,
                    values[2] as D3,
                    values[3] as D4,
                    values[4] as D5,
                    values[5] as D6,
                    values[6] as D7,
                    values[7] as D8,
                    values[8] as D9,
                    values[9] as D10,
                    values[10] as D11,
                    values[11] as D12,
                    values[12] as D13,
                    values[13] as D14,
                    values[14] as D15,
                    values[15] as D16,
                    values[16] as D17,
                    values[17] as D18,
                    values[18] as D19,
                    values[19] as D20,
                    values[20] as D21,
                    values[21] as D22,
                )
            }
    }
