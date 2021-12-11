fun main() {
    fun readGrid(input: List<String>): MutableList<MutableList<Int>> {
        val grid = mutableListOf<MutableList<Int>>()
        for (line in input) {
            grid += line.asSequence().map { it - '0' }.toMutableList()
        }
        return grid
    }

    fun dfs(grid: MutableList<MutableList<Int>>, row: Int, col: Int, flashed: MutableSet<Pair<Int, Int>>) {
        if (row !in grid.indices || col !in grid[0].indices) {
            return
        }

        // already visited or previously flashed?
        val cell = row to col
        if (grid[row][col] < 0 || cell in flashed) {
            return
        }

        grid[row][col]++
        if (grid[row][col] < 10) {
            return
        }

        grid[row][col] %= 10
        flashed += cell

        // mark the current cell as visited
        val backup = grid[row][col]
        grid[row][col] = -1
        // run DFS for 8 adjacent cells
        for (dx in -1..1) {
            for (dy in -1..1) {
                dfs(grid, row + dx, col + dy, flashed)
            }
        }
        // backtrack
        grid[row][col] = backup
    }

    fun modelFlashes(grid: MutableList<MutableList<Int>>): Int {
        val flashed = mutableSetOf<Pair<Int, Int>>()
        for (row in grid.indices) {
            for (col in grid[0].indices) {
                val cell = row to col
                if (cell in flashed) {
                    continue
                }
                if (grid[row][col] < 9) {
                    grid[row][col]++
                } else {
                    dfs(grid, row, col, flashed)
                }
            }
        }
        return flashed.size
    }

    fun part1(input: List<String>): Int {
        val grid = readGrid(input)
        var count = 0
        repeat(100) {
            count += modelFlashes(grid)
        }
        return count
    }

    fun part2(input: List<String>): Int {
        val grid = readGrid(input)
        val m = grid.size
        val n = grid[0].size

        var step = 0
        var done = false
        while (!done) {
            step++
            val numFlashedAtStep = modelFlashes(grid)
            if (numFlashedAtStep == m * n) {
                done = true
            }
        }
        return step
    }

    check(part1(readInput("Day11_test")) == 1656)
    check(part2(readInput("Day11_test")) == 195)

    val input = readInput("Day11")
    println(part1(input)) // answer = 1702
    println(part2(input)) // answer = 251
}
