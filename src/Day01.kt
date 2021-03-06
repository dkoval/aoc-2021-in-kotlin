fun main() {
    fun part1(input: List<String>): Int {
        val nums = input.map { it.toInt() }
        var count = 0
        for (i in 1 until nums.size) {
            if (nums[i] > nums[i - 1]) {
                count++
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        val nums = input.map { it.toInt() }
        var count = 0
        // we know, there are >= 3 numbers in Day01 input
        var prevSum = nums.subList(0, 3).sum()
        for (i in 1..nums.size - 3) {
            val currSum = prevSum - nums[i - 1] + nums[i + 2]
            if (currSum > prevSum) {
                count++
            }
            prevSum = currSum
        }
        return count
    }

    check(part1(readInput("Day01_test")) == 7)
    check(part2(readInput("Day01_test")) == 5)

    val input = readInput("Day01")
    println(part1(input)) // answer = 1448
    println(part2(input)) // answer = 1471
}
