import java.math.BigInteger

object BitsPacketDecoder {

    private data class PacketDecodeInfo(val value: BigInteger, val nextOffset: Int)

    private sealed class Operator(val typeId: Int) {

        companion object {
            fun of(typeId: Int): Operator = when (typeId) {
                in 0..3 -> reduceOf(typeId)
                in 5..7 -> binaryOf(typeId)
                else -> error("Unsupported type ID = $typeId")
            }

            private fun binaryOf(typeId: Int): BinaryOperator = when (typeId) {
                5 -> BinaryOperator.GreaterThan
                6 -> BinaryOperator.LessThan
                7 -> BinaryOperator.Equal
                else -> error("Unsupported type ID = $typeId")
            }

            private fun reduceOf(typeId: Int): ReduceOperator = when (typeId) {
                0 -> ReduceOperator.Sum()
                1 -> ReduceOperator.Product()
                2 -> ReduceOperator.Min()
                3 -> ReduceOperator.Max()
                else -> error("Unsupported type ID = $typeId")
            }
        }
    }

    private sealed class BinaryOperator(typeId: Int) : Operator(typeId) {
        abstract operator fun invoke(value1: BigInteger, value2: BigInteger): BigInteger

        object GreaterThan : BinaryOperator(5) {
            override fun invoke(value1: BigInteger, value2: BigInteger): BigInteger =
                if (value1 > value2) BigInteger.ONE else BigInteger.ZERO
        }

        object LessThan : BinaryOperator(6) {
            override fun invoke(value1: BigInteger, value2: BigInteger): BigInteger =
                if (value1 < value2) BigInteger.ONE else BigInteger.ZERO
        }

        object Equal : BinaryOperator(7) {
            override fun invoke(value1: BigInteger, value2: BigInteger): BigInteger =
                if (value1 == value2) BigInteger.ONE else BigInteger.ZERO
        }
    }

    private sealed class ReduceOperator(typeId: Int) : Operator(typeId) {
        abstract operator fun invoke(value: BigInteger)
        abstract val result: BigInteger

        class Sum : ReduceOperator(0) {
            private var sum = BigInteger.ZERO

            override fun invoke(value: BigInteger) {
                sum += value
            }

            override val result: BigInteger get() = sum
        }

        class Product : ReduceOperator(1) {
            private var product = BigInteger.ONE

            override fun invoke(value: BigInteger) {
                product *= value
            }

            override val result: BigInteger get() = product
        }

        class Min : ReduceOperator(2) {
            private var min: BigInteger? = null

            override fun invoke(value: BigInteger) {
                min = min?.let { minOf(it, value) } ?: value
            }

            override val result: BigInteger get() = checkNotNull(min)
        }

        class Max : ReduceOperator(3) {
            private var max: BigInteger? = null

            override fun invoke(value: BigInteger) {
                max = max?.let { maxOf(it, value) } ?: value
            }

            override val result: BigInteger get() = checkNotNull(max)
        }
    }

    fun decodePart1(binary: String): Int {
        var sumOfVersions = 0
        decode(binary, 0, hook = { version -> sumOfVersions += version })
        return sumOfVersions
    }

    fun decodePart2(binary: String): BigInteger = decode(binary, 0, hook = {}).value

    private fun decode(binary: String, offset: Int, hook: (version: Int) -> Unit): PacketDecodeInfo {
        val version = binary.substring(offset, offset + 3).toInt(radix = 2)
        hook(version)

        val typedId = binary.substring(offset + 3, offset + 6).toInt(radix = 2)
        return if (typedId == 4) {
            decodeLiteralPacket(binary, offset)
        } else {
            decodeOperatorPacket(Operator.of(typedId), binary, offset, hook)
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

    private fun decodeOperatorPacket(
        operator: Operator,
        binary: String,
        offset: Int,
        hook: (version: Int) -> Unit
    ): PacketDecodeInfo {
        // skip first 6 bits forming the header
        val lengthTypeId = binary[offset + 6]
        val numBits = when (lengthTypeId) {
            // next 15 bits are a number that represents the total length in bits of the sub-packets contained by this packet
            '0' -> 15
            // next 11 bits are a number that represents the number of sub-packets immediately contained by this packet
            '1' -> 11
            else -> error("Unsupported length type ID = $lengthTypeId")
        }

        var i = offset + 7 + numBits
        return when (operator) {
            is BinaryOperator -> {
                val values = Array<BigInteger>(2) { BigInteger.ZERO }
                repeat(2) { idx ->
                    val (value, nextOffset) = decode(binary, i, hook)
                    values[idx] = value
                    i = nextOffset
                }
                PacketDecodeInfo(operator(values[0], values[1]), i)
            }
            is ReduceOperator -> {
                val lengthTypeValue = binary.substring(offset + 7, i).toInt(radix = 2)
                when (lengthTypeId) {
                    '0' -> {
                        var numBitsToProcess = lengthTypeValue
                        println("Type ID = ${operator.typeId}, Length type ID = 0, number of bits: $numBitsToProcess")
                        while (numBitsToProcess != 0) {
                            val (value, nextOffset) = decode(binary, i, hook)
                            operator(value)
                            numBitsToProcess -= nextOffset - i
                            i = nextOffset
                        }
                        PacketDecodeInfo(operator.result, i)
                    }
                    '1' -> {
                        var numSubPacketsToProcess = lengthTypeValue
                        println("Type ID = ${operator.typeId}, Length type ID = 1, number of sub-packets: $numSubPacketsToProcess")
                        while(numSubPacketsToProcess != 0) {
                            val (value, nextOffset) = decode(binary, i, hook)
                            operator(value)
                            numSubPacketsToProcess--
                            i = nextOffset
                        }
                        PacketDecodeInfo(operator.result, i)
                    }
                    else -> error("Unsupported length type ID = $lengthTypeId")
                }
            }
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
