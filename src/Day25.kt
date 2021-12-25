fun main() {
    fun Array<CharArray>.copy(): Array<CharArray> = map { it.copyOf() }.toTypedArray()

    fun part1(input: List<String>): Int {
        var grid = input.map { it.toCharArray() }.toTypedArray()
        val numRows = grid.size
        val numCols = grid[0].size

        var count = 1
        var moving = true
        while (moving) {
            moving = false

            // state of the grid after the east-facing herd moved
            val grid2 = grid.copy()
            for (row in 0 until numRows) {
                for (col in 0 until numCols) {
                    if (grid[row][col] == '>' && grid[row][(col + 1) % numCols] == '.') {
                        grid2[row][col] = '.'
                        grid2[row][(col + 1) % numCols] = '>'
                        moving = true
                    }
                }
            }

            // state of the grid after the south-facing herd moved
            val grid3 = grid2.copy()
            for (row in 0 until numRows) {
                for (col in 0 until numCols) {
                    if (grid2[row][col] == 'v' && grid2[(row + 1) % numRows][col] == '.') {
                        grid3[row][col] = '.'
                        grid3[(row + 1) % numRows][col] = 'v'
                        moving = true
                    }
                }
            }

            grid = grid3
            if (moving) {
                count++
            }
        }
        return count
    }

    check(part1(readInput("Day25_test")) == 58)

    val input = readInput("Day25")
    println(part1(input)) // answer = 295
}
