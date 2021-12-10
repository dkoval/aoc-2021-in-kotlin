fun main() {
    data class Point(val x: Int, val y: Int)

    fun readPoint(xy: String): Point {
        val (x, y) = xy.split(",").map { it.toInt() }
        return Point(x, y)
    }

    fun readLinesOfVents(input: List<String>): Sequence<Pair<Point, Point>> =
        input.asSequence()
            .map { line -> line.split(" -> ") }
            .map { (start, end) -> readPoint(start) to readPoint(end) }

    fun incrementCountAt(counts: MutableMap<Point, Int>, x: Int, y: Int) {
        val key = Point(x, y)
        counts[key] = (counts[key] ?: 0) + 1
    }

    fun part1(input: List<String>): Int {
        val counts = mutableMapOf<Point, Int>()
        readLinesOfVents(input).forEach { (point1, point2) ->
            val (x1, y1) = point1
            val (x2, y2) = point2
            when {
                x1 == x2 -> {
                    for (y in minOf(y1, y2)..maxOf(y1, y2)) {
                        incrementCountAt(counts, x1, y)
                    }
                }
                y1 == y2 -> {
                    for (x in minOf(x1, x2)..maxOf(x1, x2)) {
                        incrementCountAt(counts, x, y1)
                    }
                }
            }
        }
        return counts.count { (_, count) -> count >= 2 }
    }

    fun part2(input: List<String>): Int {
        val counts = mutableMapOf<Point, Int>()
        readLinesOfVents(input).forEach { (point1, point2) ->
            val (x1, y1) = point1
            val (x2, y2) = point2
            when {
                x1 == x2 -> {
                    for (y in minOf(y1, y2)..maxOf(y1, y2)) {
                        incrementCountAt(counts, x1, y)
                    }
                }
                y1 == y2 -> {
                    for (x in minOf(x1, x2)..maxOf(x1, x2)) {
                        incrementCountAt(counts, x, y1)
                    }
                }
                else -> {
                    var x = x1
                    var y = y1
                    val dx = if (x1 < x2) 1 else -1
                    val dy = if (y1 < y2) 1 else -1
                    while (x != x2 + dx && y != y2 + dy) {
                        incrementCountAt(counts, x, y)
                        x += dx
                        y += dy
                    }
                }
            }
        }
        return counts.count { (_, count) -> count >= 2 }
    }

    check(part1(readInput("Day05_test")) == 5)
    check(part2(readInput("Day05_test")) == 12)

    val input = readInput("Day05")
    println(part1(input)) // answer = 5306
    println(part2(input)) // answer = 17787
}
