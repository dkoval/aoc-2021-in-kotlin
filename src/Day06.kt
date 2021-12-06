fun main() {
    fun part1(input: List<String>): Int {
        val fishes = input.first().split(",").asSequence().map { it.toInt() }.toMutableList()
        val numDays = 80

        repeat(numDays) {
            val size = fishes.size
            for (i in 0 until size) {
                if (fishes[i] > 0) {
                    fishes[i]--
                } else {
                    fishes[i] = 6
                    fishes += 8
                }
            }
        }

        // answer = 365862
        return fishes.size
    }

    fun part2(input: List<String>): Long {
        val fishes = input.first().split(",").map { it.toInt() }
        val numDays = 256

        // dp[i] denotes the number of fishes created on i-th day
        val dp = LongArray(numDays + 1)
        dp[0] = fishes.size.toLong()
        for (x in fishes) {
            dp[x + 1]++
        }

        for (i in 1..dp.lastIndex) {
            // on 8-th (countdown timer 6, 5, ..., 1, 0 takes 7 days)
            // and 10-th (countdown timer 8, 7, ..., 1, 0 takes 9 days)
            // days expect a new generation of fishes
            dp[i] += if (i > 7) dp[i - 7] else 0
            dp[i] += if (i > 9) dp[i - 9] else 0
        }

        // answer = 1653250886439
        return dp.sum()
    }

    check(part1(readInput("Day06_test")) == 5934)
    check(part2(readInput("Day06_test")) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
