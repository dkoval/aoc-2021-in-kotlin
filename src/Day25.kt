enum class Herd(val marker: Char) {
    EAST_FACING('>'),
    SOUTH_FACING('v')
}

fun main() {
    fun part1(input: List<String>): Int {
        fun moveHerd(grid: Array<CharArray>, herd: Herd): Pair<Array<CharArray>, Boolean> {
            val numRows = grid.size
            val numCols = grid[0].size

            var moved = false
            val copy = grid.map { it.copyOf() }.toTypedArray()
            for (row in 0 until numRows) {
                for (col in 0 until numCols) {
                    if (grid[row][col] == herd.marker) {
                        // can a sea cucumber move further?
                        val (nextRow, nextCol) = when (herd) {
                            Herd.EAST_FACING -> row to (col + 1) % numCols
                            Herd.SOUTH_FACING -> (row + 1) % numRows to col
                        }
                        if (grid[nextRow][nextCol] == '.') {
                            copy[row][col] = '.'
                            copy[nextRow][nextCol] = herd.marker
                            moved = true
                        }
                    }
                }
            }
            return copy to moved
        }

        // simulate moves
        var grid = input.map { it.toCharArray() }.toTypedArray()
        var count = 1
        var moving = true
        while (moving) {
            val (gridAfterEastFacingHerdMove, eastFacingHerdMoved) = moveHerd(grid, Herd.EAST_FACING)
            val (gridAfterSouthFacingHerdMove, southFacingHerdMoved) = moveHerd(gridAfterEastFacingHerdMove, Herd.SOUTH_FACING)

            grid = gridAfterSouthFacingHerdMove
            moving = eastFacingHerdMoved || southFacingHerdMoved
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
