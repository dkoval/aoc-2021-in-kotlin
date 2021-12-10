fun main() {
    fun part1(input: List<String>): Int {
        var count = 0
        for (line in input) {
            val (_, output) = line.split(" | ")
            for (s in output.split(" ")) {
                when (s.length) {
                    2, 3, 4, 7 -> count++
                }
            }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        fun sort(s: String): String = s.toCharArray().also { it.sort() }.let { String(it) }

        fun patternToDigit(patterns: List<String>): Map<String, Int> {
            val patternToDigit = mutableMapOf<String, Int>()
            val digitToPattern = mutableMapOf<Int, String>()

            // 1st pass - unique
            for (pattern in patterns) {
                when (pattern.length) {
                    2 -> {
                        val p = sort(pattern)
                        patternToDigit[p] = 1
                        digitToPattern[1] = p
                    }
                    3 -> {
                        val p = sort(pattern)
                        patternToDigit[p] = 7
                        digitToPattern[7] = p
                    }
                    4 -> {
                        val p = sort(pattern)
                        patternToDigit[p] = 4
                        digitToPattern[4] = p
                    }
                    7 -> {
                        val p = sort(pattern)
                        patternToDigit[p] = 8
                        digitToPattern[8] = p
                    }
                }
            }

            // 2nd pass - non-unique
            for (pattern in patterns) {
                when (pattern.length) {
                    // pattern of length 5 can be either 2, 3 or 5
                    5 -> {
                        val p = sort(pattern)
                        when {
                            digitToPattern[7]!!.all { c -> c in p } -> patternToDigit[p] = 3
                            digitToPattern[4]!!.count { c -> c in p } == 3 -> patternToDigit[p] = 5
                            else -> patternToDigit[p] = 2
                        }
                    }
                    // pattern of length 6 can be either 0, 6, 9
                    6 -> {
                        val p = sort(pattern)
                        when {
                            digitToPattern[4]!!.all { c -> c in p } -> patternToDigit[p] = 9
                            digitToPattern[7]!!.all { c -> c in p } -> patternToDigit[p] = 0
                            else -> patternToDigit[p] = 6
                        }
                    }
                }
            }
            return patternToDigit
        }

        var sum = 0
        for (line in input) {
            val (first, second) = line.split(" | ")
            val patterns = first.split(" ")
            val patternToDigit = patternToDigit(patterns)

            // output
            var x = 0
            val output = second.split(" ")
            for (s in output) {
                val digit = patternToDigit[sort(s)]!!
                x = x * 10 + digit
            }
            sum += x
        }
        return sum
    }

    check(part1(readInput("Day08_test")) == 26)
    check(part2(readInput("Day08_test")) == 61229)

    val input = readInput("Day08")
    println(part1(input)) // answer = 397
    println(part2(input)) // answer = 1027422
}
