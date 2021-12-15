import java.util.*

interface Grid {
    val numRows: Int
    val numCols: Int
    fun get(row: Int, col: Int): Int
}

class DefaultGrid(private val grid: List<List<Int>>) : Grid {

    override val numRows: Int = grid.size

    override val numCols: Int = grid[0].size

    override fun get(row: Int, col: Int): Int = grid[row][col]
}

class ScaledGrid(private val grid: List<List<Int>>, scaleFactor: Int) : Grid {
    private val r = grid.size
    private val c = grid[0].size

    override val numRows: Int = r * scaleFactor

    override val numCols: Int = c * scaleFactor

    override fun get(row: Int, col: Int): Int {
        var x = grid[row % r][col % c]
        // add the number of times the tile repeats to the right or downward
        x += row / r + col / c
        // risk levels above 9 wrap back around to 1
        return (x - 1) % 9 + 1
    }
}

fun main() {
    val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

    class Cell(
        val coords: Pair<Int, Int>,
        var riskLevel: Int = Int.MAX_VALUE
    )

    fun readGrid(lines: List<String>): List<List<Int>> = lines.map { line -> line.map { it - '0' } }

    fun dijkstra(grid: Grid, sourceRow: Int, sourceCol: Int, destRow: Int, destCol: Int): Int {
        // similar to BFS but with MIN heap for minimizing the total risk level
        val pq = PriorityQueue<Cell>(compareBy { cell -> cell.riskLevel })
        val source = sourceRow to sourceCol
        pq.offer(Cell(source, 0))
        val visited = mutableSetOf(source)
        while (!pq.isEmpty()) {
            val curr = pq.poll()

            val (row, col) = curr.coords
            if (row == destRow && col == destCol) {
                return curr.riskLevel
            }

            for ((dr, dc) in directions) {
                val nextRow = row + dr
                val nextCol = col + dc
                if (nextRow !in 0 until grid.numRows || nextCol !in 0 until grid.numCols) {
                    continue
                }

                val next = nextRow to nextCol
                if (next !in visited) {
                    visited += next
                    pq.offer(Cell(next, curr.riskLevel + grid.get(nextRow, nextCol)))
                }
            }
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val grid = DefaultGrid(readGrid(input))
        return dijkstra(grid, 0, 0, grid.numRows - 1, grid.numCols - 1)
    }

    fun part2(input: List<String>): Int {
        val grid = ScaledGrid(readGrid(input), 5)
        return dijkstra(grid, 0, 0, grid.numRows - 1, grid.numCols - 1)
    }

    check(part1(readInput("Day15_test")) == 40)
    check(part2(readInput("Day15_test")) == 315)

    val input = readInput("Day15")
    println(part1(input)) // answer = 656
    println(part2(input)) // answer = 2979
}
