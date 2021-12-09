import java.util.*

fun main() {
    val directions = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

    fun readGrid(input: List<String>): MutableList<MutableList<Int>> {
        val grid = mutableListOf<MutableList<Int>>()
        for (line in input) {
            grid += line.asSequence().map { it - '0' }.toMutableList()
        }
        return grid
    }

    fun part1(input: List<String>): Int {
        val grid = readGrid(input)

        val m = grid.size
        val n = grid[0].size
        var sumOfRanks = 0
        for (row in 0 until m) {
            for (col in 0 until n) {
                val neighbours = directions.asSequence()
                    .map { (dx, dy) -> row + dx to col + dy }
                    .filter { (nextRow, nextCol) -> nextRow in 0 until m && nextCol in 0 until n }
                    .toList()

                neighbours.all { (nextRow, nextCol) -> grid[row][col] < grid[nextRow][nextCol] }.also { allMatch ->
                    if (allMatch) {
                        sumOfRanks += grid[row][col] + 1
                    }
                }
            }
        }

        // answer = 575
        return sumOfRanks
    }

    fun part2(input: List<String>): Int {
        fun lowPoints(grid: List<List<Int>>): List<Pair<Int, Int>> {
            val m = grid.size
            val n = grid[0].size

            val lowPoints = mutableListOf<Pair<Int, Int>>()
            for (row in 0 until m) {
                for (col in 0 until n) {
                    val neighbours = directions.asSequence()
                        .map { (dx, dy) -> row + dx to col + dy }
                        .filter { (nextRow, nextCol) -> nextRow in 0 until m && nextCol in 0 until n }
                        .toList()

                    neighbours.all { (nextRow, nextCol) -> grid[row][col] < grid[nextRow][nextCol] }.also { allMatch ->
                        if (allMatch) {
                            lowPoints += row to col
                        }
                    }
                }
            }
            return lowPoints
        }

        fun dfs(grid: MutableList<MutableList<Int>>, row: Int, col: Int): Int {
            val m = grid.size
            val n = grid[0].size

            // check boundaries
            if (row !in 0 until m || col !in 0 until n) {
                return 0
            }

            // already visited or terminal location?
            if (grid[row][col] == -1 || grid[row][col] == 9) {
                return 0
            }

            // mark the current location as visited
            grid[row][col] = -1

            // explore a basin further out by checking 4 adjacent locations
            var area = 1
            for ((dx, dy) in directions) {
                area += dfs(grid, row + dx, col + dy)
            }
            return area
        }

        val grid = readGrid(input)
        // collect low points first
        val lowPoints = lowPoints(grid)
        // for each low point, run DFS to calculate a surrounding basin area; minHeap stores areas of the 3 largest basins
        val minHeap = PriorityQueue<Int>()
        for ((row, col) in lowPoints) {
            val area = dfs(grid, row, col)
            minHeap.offer(area)
            if (minHeap.size > 3) {
                minHeap.poll()
            }
        }

        // answer = 1019700
        return minHeap.fold(1) { ans, x -> ans * x }
    }

    check(part1(readInput("Day09_test")) == 15)
    check(part2(readInput("Day09_test")) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
