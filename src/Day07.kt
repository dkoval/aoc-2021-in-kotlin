import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        val fuel = IntArray(positions.size)
        for (i in positions.indices) {
            for (j in positions.indices) {
                fuel[i] += abs(positions[i] - positions[j])
            }
        }

        // answer = 333755
        return fuel.minOf { it }
    }

    fun part2(input: List<String>): Int {
        // 1 + 2 + ... N = N * (N + 1) / 2
        fun sumOf1ToN(n: Int) : Int = if (n % 2 == 0) n / 2 * (n + 1) else (n + 1) / 2 * n

        val positions = input.first().split(",").map { it.toInt() }
        val maxPosition = positions.maxOf { it }

        val fuel = IntArray(maxPosition + 1)
        for (position in positions) {
            for (i in fuel.indices) {
                val steps = abs(position - i)
                fuel[i] += sumOf1ToN(steps)
            }
        }

        // answer = 94017638
        return fuel.minOf { it }
    }

    check(part1(readInput("Day07_test")) == 37)
    check(part2(readInput("Day07_test")) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
