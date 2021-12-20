private const val MARGIN = 300
private const val WINDOW_SIZE = 3

fun main() {
    data class Point(val row: Int, val col: Int)

    class BigImage(
        val lightPixels: Set<Point>,
        val minRow: Int,
        val minCol: Int,
        val maxRow: Int,
        val maxCol: Int
    )

    fun readImage(grid: List<String>): BigImage {
        val numRows = grid.size
        val numCols = grid[0].length

        val lightPixels = mutableSetOf<Point>()
        var minRow = Int.MAX_VALUE
        var minCol = Int.MAX_VALUE
        var maxRow = Int.MIN_VALUE
        var maxCol = Int.MIN_VALUE
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                if (grid[row][col] == '#') {
                    lightPixels += Point(row, col)
                    minRow = minOf(minRow, row)
                    maxRow = maxOf(maxRow, row)
                    minCol = minOf(minCol, col)
                    maxCol = maxOf(maxCol, col)
                }
            }
        }
        return BigImage(
            lightPixels = lightPixels,
            minRow = minRow - MARGIN,
            minCol = minCol - MARGIN,
            maxRow = maxRow + MARGIN,
            maxCol = maxCol + MARGIN
        )
    }

    fun enhance(image: BigImage, algo: String): BigImage {
        val output = mutableSetOf<Point>()
        for (row in image.minRow..image.maxRow) {
            for (col in image.minCol..image.maxCol) {
                val bin = buildString {
                    for (dy in -1..1) {
                        for (dx in -1..1) {
                            val nextRow = row + dy
                            val nextCol = col + dx
                            append(if (Point(nextRow, nextCol) in image.lightPixels) '1' else '0')
                        }
                    }
                }
                val idx = bin.toInt(radix = 2)
                if (algo[idx] == '#') {
                    output += Point(row, col)
                }
            }
        }
        return BigImage(
            lightPixels = output,
            minRow = image.minRow + WINDOW_SIZE,
            minCol = image.minCol + WINDOW_SIZE,
            maxRow = image.maxRow - WINDOW_SIZE,
            maxCol = image.maxCol - WINDOW_SIZE
        )
    }

    fun part1(input: List<String>): Int {
        val algo = input.first()
        val grid = input.subList(2, input.size)

        var image = readImage(grid)
        repeat(2) {
            image = enhance(image, algo)
        }
        return image.lightPixels.size
    }

    fun part2(input: List<String>): Int {
        val algo = input.first()
        val grid = input.subList(2, input.size)

        var image = readImage(grid)
        repeat(50) {
            image = enhance(image, algo)
        }
        return image.lightPixels.size
    }

    check(part1(readInput("Day20_test")) == 35)
    check(part2(readInput("Day20_test")) == 3351)

    val input = readInput("Day20")
    println(part1(input)) // answer = 5486
    println(part2(input)) // answer = 20210
}
