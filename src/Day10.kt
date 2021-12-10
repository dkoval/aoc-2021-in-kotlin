import java.util.*

fun main() {
    val openToClose = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
    val closeToOpen = mapOf(')' to '(', ']' to '[', '}' to '{', '>' to '<')
    val open = closeToOpen.values.toSet()

    fun part1(input: List<String>): Int {
        fun findWrongClose(line: String): Char? {
            val stack = Stack<Char>()
            for (c in line) {
                // open bracket
                if (c in open) {
                    stack.push(c)
                } else {
                    // close bracket
                    if (stack.isEmpty() || closeToOpen[c] != stack.pop()) {
                        return c
                    }
                }
            }
            return null
        }

        val closeToPoint = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
        var totalErrorScore = 0
        for (line in input) {
            findWrongClose(line)?.also { totalErrorScore += closeToPoint[it]!! }
        }
        return totalErrorScore
    }

    fun part2(input: List<String>): Long {
        fun isCorruptedLine(line: String): Boolean {
            val stack = Stack<Char>()
            for (c in line) {
                // open bracket
                if (c in open) {
                    stack.push(c)
                } else {
                    // close bracket
                    if (stack.isEmpty() || closeToOpen[c] != stack.pop()) {
                        return true
                    }
                }
            }
            return false
        }

        fun findIncompleteOpen(line: String): Stack<Char> {
            val stack = Stack<Char>()
            for (c in line) {
                // open bracket
                if (c in open) {
                    stack.push(c)
                } else {
                    // close bracket
                    if (!stack.isEmpty() && closeToOpen[c] == stack.peek()) {
                        // pop only valid open brackets
                        stack.pop()
                    }
                }
            }
            return stack
        }

        val closeToPoint = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

        fun scoreIncompleteLine(line: String): Long {
            val stack = findIncompleteOpen(line)
            var score = 0L
            while (!stack.isEmpty()) {
                score *= 5
                score += closeToPoint[openToClose[stack.pop()]]!!
            }
            return score
        }

        val scores = mutableListOf<Long>()
        for (line in input) {
            if (isCorruptedLine(line)) {
                continue
            }
            scores += scoreIncompleteLine(line)
        }

        scores.sort()
        return scores[scores.size / 2]
    }

    check(part1(readInput("Day10_test")) == 26397)
    check(part2(readInput("Day10_test")) == 288957L)

    val input = readInput("Day10")
    println(part1(input)) // answer = 367059
    println(part2(input)) // answer = 1952146692
}
