import java.math.BigInteger

object BitsPacketDecoder {

    data class PacketDecodeInfo(val value: BigInteger, val nextOffset: Int)

    sealed class Operator {

        companion object {
            fun isBinary(typeId: Int): Boolean = typeId in 5..7

            fun binaryOf(typeId: Int): BinaryOperator = when (typeId) {
                5 -> BinaryOperator.GreaterThan
                6 -> BinaryOperator.LessThan
                7 -> BinaryOperator.Equal
                else -> error("Unsupported type ID = $typeId")
            }

            fun reduceOf(typeId: Int): ReduceOperator = when (typeId) {
                0 -> ReduceOperator.Sum()
                1 -> ReduceOperator.Product()
                2 -> ReduceOperator.Min()
                3 -> ReduceOperator.Max()
                else -> error("Unsupported type ID = $typeId")
            }
        }
    }

    sealed class BinaryOperator : Operator() {
        abstract operator fun invoke(value1: BigInteger, value2: BigInteger): BigInteger

        object GreaterThan : BinaryOperator() {
            override fun invoke(value1: BigInteger, value2: BigInteger): BigInteger =
                if (value1 > value2) BigInteger.ONE else BigInteger.ZERO
        }

        object LessThan : BinaryOperator() {
            override fun invoke(value1: BigInteger, value2: BigInteger): BigInteger =
                if (value1 < value2) BigInteger.ONE else BigInteger.ZERO
        }

        object Equal : BinaryOperator() {
            override fun invoke(value1: BigInteger, value2: BigInteger): BigInteger =
                if (value1 == value2) BigInteger.ONE else BigInteger.ZERO
        }
    }

    sealed class ReduceOperator : Operator() {
        abstract operator fun invoke(value: BigInteger)
        abstract val result: BigInteger

        class Sum : ReduceOperator() {
            private var sum = BigInteger.ZERO

            override fun invoke(value: BigInteger) {
                sum += value
            }

            override val result: BigInteger get() = sum
        }

        class Product : ReduceOperator() {
            private var product = BigInteger.ONE

            override fun invoke(value: BigInteger) {
                product *= value
            }

            override val result: BigInteger get() = product
        }

        class Min : ReduceOperator() {
            private var min: BigInteger? = null

            override fun invoke(value: BigInteger) {
                min = min?.let { minOf(it, value) } ?: value
            }

            override val result: BigInteger get() = checkNotNull(min)
        }

        class Max : ReduceOperator() {
            private var max: BigInteger? = null

            override fun invoke(value: BigInteger) {
                max = max?.let { maxOf(it, value) } ?: value
            }

            override val result: BigInteger get() = checkNotNull(max)
        }
    }

    fun decodePart1(binary: String): Int {
        var sumOfVersions = 0
        decode(binary) { version -> sumOfVersions += version }
        return sumOfVersions
    }

    fun decodePart2(binary: String): BigInteger = decode(binary).value

    private fun decode(binary: String, offset: Int = 0, hook: (version: Int) -> Unit = {}): PacketDecodeInfo {
        val version = binary.substring(offset, offset + 3).toInt(radix = 2)
        hook(version)

        val typedId = binary.substring(offset + 3, offset + 6).toInt(radix = 2)
        return if (typedId == 4) {
            decodeLiteralPacket(binary, offset)
        } else {
            decodeOperatorPacket(binary, offset, typedId, hook)
        }
    }

    private fun decodeLiteralPacket(binary: String, offset: Int): PacketDecodeInfo {
        // skip first 6 bits forming the header
        var i = offset + 6
        // read groups of 5 bits each to form a binary number
        val num = buildString {
            var done = false
            while (!done) {
                // read the group prefix bit
                done = binary[i] == '0'
                // read next 4 bits
                this.append(binary.substring(i + 1, i + 5))
                i += 5
            }
        }
        return PacketDecodeInfo(num.toBigInteger(radix = 2), i).also { println(it) }
    }

    private fun decodeOperatorPacket(binary: String, offset: Int, typedId: Int, hook: (version: Int) -> Unit): PacketDecodeInfo {
        // skip first 6 bits forming the header
        var i = offset + 6
        return when (val lengthTypeId = binary[i]) {
            '0' -> {
                // next 15 bits are a number that represents the total length in bits of the sub-packets contained by this packet
                var numBits = binary.substring(i + 1, i + 16).toInt(radix = 2)
                println("Type ID = $typedId, Length type ID = 0, number of bits: $numBits")

                i += 16
                if (Operator.isBinary(typedId)) {
                    val operator = Operator.binaryOf(typedId)
                    val values = Array<BigInteger>(2) { BigInteger.ZERO }
                    repeat(2) { idx ->
                        val (value, nextOffset) = decode(binary, i, hook)
                        values[idx] = value
                        i = nextOffset
                    }
                    PacketDecodeInfo(operator(values[0], values[1]), i)
                } else {
                    val operator = Operator.reduceOf(typedId)
                    while (numBits != 0) {
                        val (value, nextOffset) = decode(binary, i, hook)
                        operator(value)
                        numBits -= nextOffset - i
                        i = nextOffset
                    }
                    PacketDecodeInfo(operator.result, i)
                }
            }
            '1' -> {
                // next 11 bits are a number that represents the number of sub-packets immediately contained by this packet
                val numSubPackets = binary.substring(i + 1, i + 12).toInt(radix = 2)
                println("Type ID = $typedId, Length type ID = 1, number of sub-packets: $numSubPackets")

                i += 12
                if (Operator.isBinary(typedId)) {
                    val operator = Operator.binaryOf(typedId)
                    val values = Array<BigInteger>(2) { BigInteger.ZERO }
                    repeat(2) { idx ->
                        val (value, nextOffset) = decode(binary, i, hook)
                        values[idx] = value
                        i = nextOffset
                    }
                    PacketDecodeInfo(operator(values[0], values[1]), i)
                } else {
                    val operator = Operator.reduceOf(typedId)
                    repeat(numSubPackets) {
                        val (value, nextOffset) = decode(binary, i, hook)
                        operator(value)
                        i = nextOffset
                    }
                    PacketDecodeInfo(operator.result, i)
                }
            }
            else -> error("Unsupported length type ID = $lengthTypeId")
        }
    }
}

fun main() {
    val hexToBin = mutableMapOf(
        '0' to "0000",
        '1' to "0001",
        '2' to "0010",
        '3' to "0011",
        '4' to "0100",
        '5' to "0101",
        '6' to "0110",
        '7' to "0111",
        '8' to "1000",
        '9' to "1001",
        'A' to "1010",
        'B' to "1011",
        'C' to "1100",
        'D' to "1101",
        'E' to "1110",
        'F' to "1111"
    )

    fun readBinary(hexadecimal: String): String = buildString {
        for (c in hexadecimal) {
            append(hexToBin[c]!!)
        }
    }

    fun part1(input: List<String>): Int {
        val binary = readBinary(input[0]).also { println("Binary: $it") }
        return BitsPacketDecoder.decodePart1(binary)
    }

    fun part2(input: List<String>): BigInteger {
        val binary = readBinary(input[0]).also { println("Binary: $it") }
        return BitsPacketDecoder.decodePart2(binary)
    }

    println("*** Tests: Part 1 ***")
    check(part1(readInput("Day16_test_part1_1")) == 16)
    check(part1(readInput("Day16_test_part1_2")) == 12)
    check(part1(readInput("Day16_test_part1_3")) == 23)
    check(part1(readInput("Day16_test_part1_4")) == 31)

    println("\n*** Tests: Part 2 ***")
    check(part2(readInput("Day16_test_part2_1")) == 3.toBigInteger())
    check(part2(readInput("Day16_test_part2_2")) == 54.toBigInteger())
    check(part2(readInput("Day16_test_part2_3")) == 7.toBigInteger())
    check(part2(readInput("Day16_test_part2_4")) == 9.toBigInteger())
    check(part2(readInput("Day16_test_part2_5")) == 1.toBigInteger())
    check(part2(readInput("Day16_test_part2_6")) == 0.toBigInteger())
    check(part2(readInput("Day16_test_part2_7")) == 0.toBigInteger())
    check(part2(readInput("Day16_test_part2_8")) == 1.toBigInteger())

    println("\n*** Answers ***")
    val input = readInput("Day16")
    println("Part 1. Sum of packet versions = ${part1(input)}") // answer = 879
    println("Part 2. Packet value = ${part2(input)}")           // answer = 539051801941
}
