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

        // answer = 397
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
                        val normalizedPattern = sort(pattern)
                        patternToDigit[normalizedPattern] = 7
                        digitToPattern[7] = normalizedPattern
                    }
                    4 -> {
                        val normalizedPattern = sort(pattern)
                        patternToDigit[normalizedPattern] = 4
                        digitToPattern[4] = normalizedPattern
                    }
                    7 -> {
                        val normalizedPattern = sort(pattern)
                        patternToDigit[normalizedPattern] = 8
                        digitToPattern[8] = normalizedPattern
                    }
                }
            }

            // 2nd pass - non-unique
            for (pattern in patterns) {
                when (pattern.length) {
                    // pattern of length 5 can be either 2, 3 or 5
                    5 -> {
                        val normalizedPattern = sort(pattern)
                        when {
                            digitToPattern[7]!!.all { c -> c in normalizedPattern } -> patternToDigit[normalizedPattern] = 3
                            digitToPattern[4]!!.count { c -> c in normalizedPattern } == 3 -> patternToDigit[normalizedPattern] = 5
                            else -> patternToDigit[normalizedPattern] = 2
                        }
                    }
                    // pattern of length 6 can be either 0, 6, 9
                    6 -> {
                        val normalizedPattern = sort(pattern)
                        when {
                            digitToPattern[4]!!.all { c -> c in normalizedPattern } -> patternToDigit[normalizedPattern] = 9
                            digitToPattern[7]!!.all { c -> c in normalizedPattern } -> patternToDigit[normalizedPattern] = 0
                            else -> patternToDigit[normalizedPattern] = 6
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

        // answer = 1027422
        return sum
    }

    check(part1(readInput("Day08_test")) == 26)
    check(part2(readInput("Day08_test")) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
