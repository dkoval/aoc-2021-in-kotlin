import java.util.*

enum class Direction {
    UP, LEFT
}

fun main() {
    class Grid {
        private val dots = sortedMapOf<Int, SortedSet<Int>>()
        private var lastRow = -1
        private var lastCol = -1

        val numRows: Int get() = lastRow + 1
        val numCols: Int get() = lastCol + 1

        fun isSet(row: Int, col: Int): Boolean = row in dots && col in dots[row]!!

        fun set(row: Int, col: Int): Boolean {
            lastRow = maxOf(lastRow, row)
            lastCol = maxOf(lastCol, col)
            return dots.getOrPut(row) { sortedSetOf() }.add(col)
        }

        fun unset(row: Int, col: Int): Boolean {
            if (row in dots) {
                return dots[row]!!.remove(col)
            }
            return false
        }

        fun print(endRow: Int, endCol: Int) {
            for (row in 0..endRow) {
                for (col in 0..endCol) {
                    print(if (isSet(row, col)) '@' else '.')
                }
                println()
            }
        }
    }

    fun readGrid(lines: List<String>): Grid =
        Grid().apply {
            lines.asSequence()
                .map { line -> line.split(",").map { x -> x.toInt() } }
                .forEach { (x, y) -> this.set(y, x) }
        }

    fun readInstructions(lines: List<String>, limit: Int = Int.MAX_VALUE): List<Pair<Direction, Int>> =
        lines.asSequence()
            .map { line -> line.removePrefix("fold along ").split("=") }
            .map { (direction, shift) ->
                when (direction) {
                    "x" -> Direction.LEFT to shift.toInt()
                    "y" -> Direction.UP to shift.toInt()
                    else -> error("Unsupported direction: $direction")
                }
            }
            .take(limit)
            .toList()

    fun part1(input: List<String>): Int {
        fun foldUp(grid: Grid, y: Int): Int {
            val numDots = minOf(y, grid.numRows - y - 1)
            var count = 0
            for (offsetY in 0 until numDots) {
                for (col in 0 until grid.numCols) {
                    if (grid.isSet(y - offsetY - 1, col) || grid.isSet(y + offsetY + 1, col)) {
                        count++
                    }
                }
            }
            return count
        }

        fun foldLeft(grid: Grid, x: Int): Int {
            val numDots = minOf(x, grid.numCols - x - 1)
            var count = 0
            for (row in 0 until grid.numRows) {
                for (offsetX in 0 until numDots) {
                    if (grid.isSet(row, x - offsetX - 1) || grid.isSet(row, x + offsetX + 1)) {
                        count++
                    }
                }
            }
            return count
        }

        val newLine = input.indexOfFirst { it.isEmpty() }
        val grid = readGrid(input.subList(0, newLine))
        val (direction, shift) = readInstructions(input.subList(newLine + 1, input.size), limit = 1).first()
        return when (direction) {
            Direction.UP -> foldUp(grid, shift)
            Direction.LEFT -> foldLeft(grid, shift)
        }
    }

    fun part2(input: List<String>) {
        fun foldUp(grid: Grid, y: Int, endRow: Int, endCol: Int): Int {
            val numDots = minOf(y, endRow - y)
            for (offset in 0 until numDots) {
                for (col in 0..endCol) {
                    if (grid.isSet(y + offset + 1, col)) {
                        grid.unset(y + offset + 1, col)
                        grid.set(y - offset - 1, col)
                    }
                }
            }
            return y - 1
        }

        fun foldLeft(grid: Grid, x: Int, endRow: Int, endCol: Int): Int {
            val numDots = minOf(x, endCol - x)
            for (row in 0..endRow) {
                for (offset in 0 until numDots) {
                    if (grid.isSet(row, x + offset + 1)) {
                        grid.unset(row, x + offset + 1)
                        grid.set(row, x - offset - 1)
                    }
                }
            }
            return x - 1
        }

        val newLine = input.indexOfFirst { it.isEmpty() }
        val grid = readGrid(input.subList(0, newLine))
        val instructions = readInstructions(input.subList(newLine + 1, input.size))

        var endRow = grid.numRows - 1
        var endCol = grid.numCols - 1
        for ((direction, shift) in instructions) {
            when (direction) {
                Direction.UP -> endRow = foldUp(grid, shift, endRow, endCol)
                Direction.LEFT -> endCol = foldLeft(grid, shift, endRow, endCol)
            }
        }
        grid.print(endRow, endCol)
    }

    println("*** Tests ***")
    check(part1(readInput("Day13_test")) == 17)
    part2(readInput("Day13_test"))

    println("*** Answers ***")
    val input = readInput("Day13")
    println(part1(input)) // answer = 731
    part2(input)          // answer = ZKAUCFUC
}
