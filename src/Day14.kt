class ListNode(var value: Char) {
    var next: ListNode? = null

    companion object {
        fun listOf(s: String): ListNode? {
            val dummy = ListNode('#')
            var curr = dummy
            for (c in s) {
                curr.next = ListNode(c)
                curr = curr.next!!
            }
            return dummy.next
        }
    }
}

fun main() {
    fun readRules(lines: List<String>): Map<String, Char> =
        lines.asSequence()
            .map { it.split(" -> ") }
            .map { (first, second) -> first to second.first() }
            .toMap()

    fun part1(input: List<String>): Int {
        val template = input.first()
        val rules = readRules(input.subList(2, input.size))

        val head = ListNode.listOf(template)!!
        val counts = template.groupingBy { it }.eachCount().toMutableMap()
        repeat(10) {
            var curr = head
            while (curr.next != null) {
                val next = curr.next!!
                val pair = "${curr.value}${next.value}"
                if (pair in rules) {
                    val c = rules[pair]!!
                    val insert = ListNode(c)
                    curr.next = insert
                    insert.next = next
                    counts[c] = (counts[c] ?: 0) + 1
                }
                curr = next
            }
        }

        return counts.values
            .fold(Int.MIN_VALUE to Int.MAX_VALUE) { (max, min), x -> maxOf(max, x) to minOf(min, x) }
            .let { (max, min) -> max - min }
    }

    fun part2(input: List<String>): Long {
        fun counts(s: String): MutableMap<Char, Long> {
            val counts = mutableMapOf<Char, Long>()
            for (c in s) {
                counts[c] = (counts[c] ?: 0) + 1
            }
            return counts
        }

        fun pairsOf(s: String, validPairs: Map<String, Char>): MutableMap<String, Long> {
            val pairs = mutableMapOf<String, Long>()
            for (i in 0..s.length - 2) {
                val pair = "${s[i]}${s[i + 1]}"
                if (pair in validPairs) {
                    pairs[pair] = (pairs[pair] ?: 0) + 1
                }
            }
            return pairs
        }

        val template = input.first()
        val rules = readRules(input.subList(2, input.size))

        var pairs = pairsOf(template, rules)
        val counts = counts(template)
        repeat(40) {
            val newPairs = mutableMapOf<String, Long>()
            for ((pair, count) in pairs) {
                val c = rules[pair]!!
                counts[c] = (counts[c] ?: 0) + count

                // generate new pairs
                for (i in 0..1) {
                    val newPair = if (i == 0) "${pair[i]}$c" else "$c${pair[i]}"
                    if (newPair in rules) {
                        newPairs[newPair] = (newPairs[newPair] ?: 0) + count
                    }
                }
            }
            pairs = newPairs
        }

        return counts.values
            .fold(Long.MIN_VALUE to Long.MAX_VALUE) { (max, min), x -> maxOf(max, x) to minOf(min, x) }
            .let { (max, min) -> max - min }
    }

    check(part1(readInput("Day14_test")) == 1588)
    check(part2(readInput("Day14_test")) == 2188189693529L)

    val input = readInput("Day14")
    println(part1(input)) // answer = 3058
    println(part2(input)) // answer = 3447389044530
}
